package com.github.gantleman.shopd.service.impl;

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
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("cateService")
public class CateServiceImpl implements CateService {

    @Autowired(required = false)
    CategoryMapper categoryMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private CategoryJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Category";
    
    @PostConstruct
    public void init() {
        if (!cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, CategoryJob.class, null, job);
        }
    }

    @Override
    public List<Category> selectByAll() {
        List<Category> re = new ArrayList<Category>();
        if(redisu.hasKey(classname)) {
            //read redis
            Map<Object, Object> rm = redisu.hmget(classname);
            for (Object value : rm.values()) {
                Category tovalue = (Category)value;
                re.add( tovalue );

                redisu.expire("Category_i"+tovalue.getCatename(), 0);
            }
            redisu.expire(classname, 0);
        }else {
            ///init, only once
            //write redis
            Map<String, Object> tmap = new HashMap<>();
            re = categoryMapper.selectByExample(new CategoryExample());

            ///read and write
            if(!redisu.hasKey(classname)) {
                for (Category value : re) {
                    tmap.put(value.getCateid().toString(), (Object)value);
                    redisu.sAddAndTime("Category_i"+value.getCatename(), 0, value.getCateid());
                }
                redisu.hmset(classname, tmap, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Category> selectByNameForRead(String cate) {
        List<Category> re = new ArrayList<>();
        //index Category_i id to name is more to one
        if(redisu.hasKey("Category_i"+cate)) {
            //read redis
            Set<Object> o = redisu.sGet("Category_i"+cate);
            for(Object oi: o) {
                Integer i = (Integer) oi;                    
                Category rc = (Category) redisu.hget(classname, i.toString());

                if (rc != null)
                    re.add(rc);
            }

            redisu.expire("Category_i", 0);
            redisu.expire(classname, 0);
        }else {
            ///init, only once
            if(!redisu.hasKey(classname)) {
                Map<String, Object> tmap = new HashMap<>();
                re = categoryMapper.selectByExample(new CategoryExample());
    
                ///read and write
                if(!redisu.hasKey(classname)) {
                    for (Category value : re) {
                        tmap.put(value.getCateid().toString(), (Object)value);
                        redisu.sAddAndTime("Category_i"+value.getCatename(), 0, value.getCateid());
                    }
                    redisu.hmset(classname, tmap, 0);
                }                
            }
        }
        return re;
    }

    @Override
    public List<Category> selectByName(String cate) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        List<Category> category = categoryDA.findAllChatByCategoryName(cate);
        return category;
    }

    @Override
    public void insertSelective(Category category) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        category.setCateid(new Long(id).intValue());
        category.MakeStamp();
        category.setStatus(2);
        categoryDA.saveCategory(category);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Category_i" + category.getCatename(), 0, category.getCateid()); 
        redisu.hset(classname, category.getCateid().toString(), category, 0);
    }
    
    @Override
    public Category selectById(Integer category) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        return categoryDA.findCategoryById(category);
    }

    @Override
    public void updateByPrimaryKeySelective(Category category) {
        ///init
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        Category lcategory = categoryDA.findCategoryById(category.getCateid());

        lcategory.MakeStamp();
        lcategory.setStatus(3);
        
        if(category.getCatename() != null){
            lcategory.setCatename(category.getCatename());
        }
        categoryDA.saveCategory(lcategory);

        //Re-publish to redis
        redisu.hset(classname, lcategory.getCateid().toString(), (Object)lcategory, 0);

    }

    @Override
    public void deleteByPrimaryKey(Integer cateid) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        Category category = categoryDA.findCategoryById(cateid);
 
        if (category != null)
        {
             category.MakeStamp();
             category.setStatus(1);
             categoryDA.saveCategory(category);
 
             //Re-publish to redis
             redisu.hdel(classname, category.getCateid().toString());
        } 
    }


    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());
        List<Category> lcategory = categoryDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Category category : lcategory) {
            if(null ==  category.getStatus()) {
                categoryDA.removedCategoryById(category.getCateid());
            }

            if(1 ==  category.getStatus() && 1 == categoryMapper.deleteByPrimaryKey(category.getCateid())) {
                categoryDA.removedCategoryById(category.getCateid());
            }

            if(2 ==  category.getStatus()  && 1 == categoryMapper.insert(category)) {
                categoryDA.removedCategoryById(category.getCateid());
            }

            if(3 ==  category.getStatus() && 1 == categoryMapper.updateByPrimaryKey(category)) {
                categoryDA.removedCategoryById(category.getCateid());
            }
        }

        if (categoryDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        BDBEnvironmentManager.getInstance();
        CategoryDA categoryDA=new CategoryDA(BDBEnvironmentManager.getMyEntityStore());

        if (cacheService.IsCache(classname)) {
            ///init
            List<Category> re = new ArrayList<Category>();
            Map<String, Object> tmap = new HashMap<>();

            re = categoryMapper.selectByExample(new CategoryExample());
            for (Category value : re) {
                tmap.put(value.getCateid().toString(), (Object)value);

                value.MakeStamp();
                categoryDA.saveCategory(value);

                redisu.sAddAndTime("Category_i"+value.getCatename().toString(), 0, value.getCateid()); 
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            quartzManager.addJob(classname,classname,classname,classname, CategoryJob.class, null, job);

            redisu.hmset(classname, tmap, 0);
        }        
    }
}
