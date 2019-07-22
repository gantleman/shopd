package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.CategoryDA;
import com.github.gantleman.shopd.dao.CategoryMapper;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.entity.CategoryExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.CateService;
import com.github.gantleman.shopd.service.jobs.CategoryJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cateService")
public class CateServiceImpl implements CateService {

    @Autowired(required = false)
    CategoryMapper categoryMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private CategoryJob job;
    
    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Category";
    
    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, CategoryJob.class, null, job);
        }
    }

    @Override
    public List<Category> selectByAll(Integer pageId, String url) {
        List<Category> re = new ArrayList<Category>();

        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Category r = (Category) redisu.hget(classname, pageId.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/categorypage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                Integer i = cacheService.PageBegin(pageId);
                Integer l = cacheService.PageEnd(pageId);
                for(;i < l; i++){
                    Category r = (Category) redisu.hget(classname, pageId.toString());
                    if(r != null)
                        re.add(r);
                }
    
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public List<Category> selectByNameForRead(String cate, String url) {
        List<Category> re = new ArrayList<Category>();

        if(redisu.hHasKey("category_u", cate)) {
            //read redis
            Object id = redisu.hget("category_u", cate);
            Category r =  selectById((Integer)id, url);
            if (r != null)
                re.add(r);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/catepagename", cate);
            }else{
                RefreshDBD(cate, true);
            }
            if(redisu.hHasKey("category_u", cate)) {
                //read redis
                Object id = redisu.hget("category_u", cate);
                if(id != null){
                    Category r =  selectById((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
            }
        }
        return re;
    }

    @Override
    public Category selectById(Integer categoryid, String url) {
        Category re = null;
        Integer pageId = cacheService.PageID(categoryid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Object o = redisu.hget(classname, categoryid.toString());
            if(o != null){
                re = (Category) o;
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/categorypage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                Object o = redisu.hget(classname, categoryid.toString());
                if(o != null){
                    re = (Category) o;
                    redisu.hincr(classname+"pageid", pageId.toString(), 1);
                }
            }    
        }
        return re;
    }

    @Override
    public List<Category> selectByName(String catename) {
        //Because the name cannot be changed to page id, 
        //every retrieval failure triggers the retrieval database.
        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        List<Category> category = categoryDA.findAllChatByCategoryName(catename);
        if(category.isEmpty()){
            CategoryExample categoryExample = new CategoryExample();
            categoryExample.or().andCatenameEqualTo(catename);
            category = categoryMapper.selectByExample(categoryExample);
            
            for(Category ca : category){
                RefreshDBD(cacheService.PageID(ca.getCateid()), false);
            }
        }
        return category;
    }

    @Override
    public void insertSelective(Category category) {
        RefreshDBD(cacheService.PageID(category.getCateid()), false);

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        category.setCateid(new Long(id).intValue());
        category.setStatus(CacheService.STATUS_INSERT);
        categoryDA.saveCategory(category);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset("category_u", category.getCatename(), category.getCateid());
        redisu.hset(classname, category.getCateid().toString(), category);
    }

    @Override
    public void updateByPrimaryKeySelective(Category category) {
        ///init
        RefreshDBD(cacheService.PageID(category.getCateid()), false);

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        Category lcategory = categoryDA.findCategoryById(category.getCateid());

        if(lcategory.getStatus()== null){
            lcategory.setStatus(CacheService.STATUS_UPDATE);
        }

        lcategory.setCatename(category.getCatename());
        categoryDA.saveCategory(lcategory);

        //Re-publish to redis
        redisu.hset("category_u", lcategory.getCatename(), lcategory.getCateid());
        redisu.hset(classname, lcategory.getCateid().toString(), (Object)lcategory);
    }

    @Override
    public void deleteByPrimaryKey(Integer categoryid) {
        RefreshDBD(cacheService.PageID(categoryid), false);

       BDBEnvironmentManager.getInstance();
       CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
       Category category = categoryDA.findCategoryById(categoryid);

       if(category != null && category.getStatus() == CacheService.STATUS_INSERT){
            categoryDA.removedCategoryById(categoryid);
            //Re-publish to redis
            redisu.hdel(classname, category.getCateid().toString());
            redisu.hdel("category_u", category.getCatename());
       } else if (category != null)
       {
            category.setStatus(CacheService.STATUS_DELETE);
            categoryDA.saveCategory(category);

            //Re-publish to redis
            redisu.hdel("category_u", category.getCatename());
            redisu.hdel(classname, category.getCateid().toString());
       }  
    }


    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Category category = categoryDA.findCategoryById(i);
                if(category != null){
                    if(null ==  category.getStatus()) {
                        categoryDA.removedCategoryById(category.getCateid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  category.getStatus() && 1 == categoryMapper.deleteByPrimaryKey(category.getCateid())) {
                        categoryDA.removedCategoryById(category.getCateid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  category.getStatus()  && 1 == categoryMapper.insert(category)) {
                        categoryDA.removedCategoryById(category.getCateid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  category.getStatus() && 1 == categoryMapper.updateByPrimaryKey(category)) {
                        categoryDA.removedCategoryById(category.getCateid());
                    } 
                    redisu.hdel("category_u", category.getCatename());
                    redisu.hdel(classname, category.getCateid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (categoryDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID,classname, CategoryJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Category> re = new ArrayList<Category>();          
            CategoryExample categoryExample = new CategoryExample();
            categoryExample.or().andCateidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            categoryExample.or().andCateidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = categoryMapper.selectByExample(categoryExample);
            for (Category value : re) {
                redisu.hset("category_u", value.getCatename(), value.getCateid());
                redisu.hset(classname, value.getCateid().toString(), value);
                categoryDA.saveCategory(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Category r = categoryDA.findCategoryById(i);
                    if(r != null && r.getStatus() != CacheService.STATUS_DELETE)
                    redisu.hset("category_u", r.getCatename(), r.getCateid());
                    redisu.hset(classname, i.toString(), r);                        
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshDBD(String name, boolean refresRedis) {
        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        List<Category> category = categoryDA.findAllChatByCategoryName(name);
        if(category.isEmpty()){
            CategoryExample categoryExample = new CategoryExample();
            categoryExample.or().andCatenameEqualTo(name);
            category = categoryMapper.selectByExample(categoryExample);
            
            for(Category ca : category){
                RefreshDBD(cacheService.PageID(ca.getCateid()), refresRedis);
            }     
        }
    }
}
