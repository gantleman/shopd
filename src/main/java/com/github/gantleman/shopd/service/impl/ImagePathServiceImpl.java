package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.ImagePathDA;
import com.github.gantleman.shopd.da.ImagepathGoodsDA;
import com.github.gantleman.shopd.dao.ImagePathMapper;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.ImagePathExample;
import com.github.gantleman.shopd.entity.ImagepathGoods;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.jobs.ImagePathJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("imagePathService")
public class ImagePathServiceImpl implements ImagePathService {

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ImagePathJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "ImagePath";

    private String classname_extra = "ImagePath_Goods";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, ImagePathJob.class, null, job);
        }
    }

    public void insertSelective_extra(ImagePath imagePath) {
        //add to ImagepathGoodsDA
        RefreshUserDBD(imagePath.getGoodid(), false, false);
        BDBEnvironmentManager.getInstance();
        ImagepathGoodsDA imagePathGoodsDA=new ImagepathGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        ImagepathGoods imagePathGoods = imagePathGoodsDA.findImagepathGoodsById(imagePath.getGoodid());
        if(imagePathGoods == null){
            imagePathGoods = new ImagepathGoods();
        }
        imagePathGoods.addImagePathList(imagePath.getPathid());
        imagePathGoodsDA.saveImagepathGoods(imagePathGoods);

        //Re-publish to redis
        redisu.sAdd("imagepath_g" + imagePath.getGoodid().toString(), imagePath.getPathid()); 
    }

    @Override
    public void insertImagePath(ImagePath imagePath) {
        BDBEnvironmentManager.getInstance();
        ImagePathDA imagePathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        imagePath.setPathid(new Long(id).intValue());
        imagePath.setStatus(CacheService.STATUS_INSERT);
        imagePathDA.saveImagePath(imagePath);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, imagePath.getPathid().toString(), (Object)imagePath, 0);

        insertSelective_extra(imagePath);
    }

    @Override
    public ImagePath getImagepathByKey(Integer imagePathid, String url) {
        ImagePath re = null;
        Integer pageId = cacheService.PageID(imagePathid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (ImagePath) redisu.hget(classname, imagePathid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/imagepathpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname, imagePathid.toString())) {
                //read redis
                re = (ImagePath) redisu.hget(classname, imagePathid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public List<ImagePath> findImagePath(Integer goodsid, String url) {
        List<ImagePath> re = null;

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(goodsid).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("imagepath_g"+goodsid.toString());
            if(ro != null){
                re = new ArrayList<ImagePath>();
                for (Object id : ro) {
                    ImagePath r =  getImagepathByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsid).toString(), 1);
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/imagepathuserpage", goodsid);
            }else{
                RefreshUserDBD(goodsid, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(goodsid).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("imagepath_g"+goodsid.toString());
                if(ro != null){
                    re = new ArrayList<ImagePath>();
                    for (Object id : ro) {
                        ImagePath r =  getImagepathByKey((Integer)id, url);
                        if (r != null)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(goodsid).toString(), 1);
                }
            }
        }
        return re;
    }


    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ImagepathGoodsDA imagePathGoodsDA=new ImagepathGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ImagepathGoods imagePathGoods = imagePathGoodsDA.findImagepathGoodsById(i);
                if(imagePathGoods != null){
                    imagePathGoodsDA.removedImagepathGoodsById(imagePathGoods.getGoodsid());
                    redisu.del("imagepath_g"+imagePathGoods.getGoodsid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (imagePathGoodsDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ImagePathDA imagePathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ImagePath imagePath = imagePathDA.findImagePathById(i);
                if(imagePath != null){
                    if(null ==  imagePath.getStatus()) {
                        imagePathDA.removedImagePathById(imagePath.getPathid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  imagePath.getStatus() && 1 == imagePathMapper.deleteByPrimaryKey(imagePath.getPathid())) {
                        imagePathDA.removedImagePathById(imagePath.getPathid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  imagePath.getStatus()  && 1 == imagePathMapper.insert(imagePath)) {
                        imagePathDA.removedImagePathById(imagePath.getPathid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  imagePath.getStatus() && 1 == imagePathMapper.updateByPrimaryKey(imagePath)) {
                        imagePathDA.removedImagePathById(imagePath.getPathid());
                    }
                    redisu.hdel(classname, imagePath.getPathid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (imagePathDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, ImagePathJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            ImagePathDA imagePathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<ImagePath> re = new ArrayList<ImagePath>();          
            ImagePathExample imagePathExample = new ImagePathExample();
            imagePathExample.or().andPathidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andPathidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = imagePathMapper.selectByExample(imagePathExample);
            for (ImagePath value : re) {
                redisu.hset(classname, value.getPathid().toString(), value);
                imagePathDA.saveImagePath(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                ImagePathDA imagePathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    ImagePath r = imagePathDA.findImagePathById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        ImagepathGoodsDA imagePathGoodsDA=new ImagepathGoodsDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<ImagePath> re = new ArrayList<ImagePath>();          
            ImagePathExample imagePathExample = new ImagePathExample();
            imagePathExample.or().andGoodidGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andGoodidLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = imagePathMapper.selectByExample(imagePathExample);
            for (ImagePath value : re) {
                ImagepathGoods imagepathGoods  = imagePathGoodsDA.findImagepathGoodsById(value.getPathid());
                if(imagepathGoods == null){
                    imagepathGoods = new ImagepathGoods();
                }
                
                redisu.sAdd("imagepath_g"+value.getGoodid().toString(), (Object)value.getPathid());

                if(andAll){ 
                    RefreshDBD(cacheService.PageID(value.getPathid()), refresRedis);
                }

                imagePathGoodsDA.saveImagepathGoods(imagepathGoods);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    ImagepathGoods r = imagePathGoodsDA.findImagepathGoodsById(i);
                    if(r!= null){
                        List<Integer> li = r.getImagepathList();
                        for(Integer id: li){
                            redisu.sAdd("imagepath_g"+r.getGoodsid().toString(), (Object)id);
                        }
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
