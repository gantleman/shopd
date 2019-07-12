package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.ImagePathDA;
import com.github.gantleman.shopd.dao.ImagePathMapper;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.ImagePathExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.jobs.ImagePathJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("imagePathService")
public class ImagePathServiceImpl implements ImagePathService {

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private ImagePathJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "ImagePath";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, ImagePathJob.class, null, job);
        }
    }

    @Override
    public void insertImagePath(ImagePath imagepath) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        ImagePathDA imagepathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        imagepath.setPathid(new Long(id).intValue());
        imagepath.MakeStamp();
        imagepath.setStatus(2);
        imagepathDA.saveImagePath(imagepath);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, imagepath.getPathid().toString(), imagepath, 0);
    }

    @Override
    public List<ImagePath> findImagePath(Integer goodsid) {
        List<ImagePath> re = new ArrayList<ImagePath>();
        if(redisu.hasKey("ImagePath_n"+goodsid)) {

            //read redis
            Set<Object> rm = redisu.sGet("ImagePath_n"+goodsid);

            for(Object value: rm) {
                Object hr = redisu.hget(classname, ((Integer)value).toString());
                if(hr != null)
                    re.add((ImagePath)hr);
            }
            redisu.expire("ImagePath_n"+goodsid, 0);
            redisu.expire(classname, 0);
        }else {
            if(redisu.hasKey(classname)){
                //write redis
                Map<String, Object> tmap = new HashMap<>();
                List<ImagePath> lre = new ArrayList<ImagePath>();
                lre = imagePathMapper.selectByExample(new ImagePathExample());
                for (ImagePath value : lre) {
                    tmap.put(value.getPathid().toString(), (Object)value);
                    redisu.sAddAndTime("ImagePath_n"+value.getGoodid(), 0, value.getPathid());
                    re.add(value);
                }
                ///read and write
                if(!redisu.hasKey(classname)) {
                    redisu.hmset(classname, tmap, 0);
                }                 
            }
        }
        return re;
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        ImagePathDA imagepathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());
        List<ImagePath> limagepath = imagepathDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (ImagePath imagepath : limagepath) {
            if(null ==  imagepath.getStatus()) {
                imagepathDA.removedImagePathById(imagepath.getPathid());
            }

            if(1 ==  imagepath.getStatus() && 1 == imagePathMapper.deleteByPrimaryKey(imagepath.getPathid())) {
                imagepathDA.removedImagePathById(imagepath.getPathid());
            }

            if(2 ==  imagepath.getStatus()  && 1 == imagePathMapper.insert(imagepath)) {
                imagepathDA.removedImagePathById(imagepath.getPathid());
            }

            if(3 ==  imagepath.getStatus() && 1 == imagePathMapper.updateByPrimaryKey(imagepath)) {
                imagepathDA.removedImagePathById(imagepath.getPathid());
            }
        }

        if (imagepathDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        ///init
       if (cacheService.IsCache(classname)) {
           BDBEnvironmentManager.getInstance();
           ImagePathDA imagepathDA=new ImagePathDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<ImagePath> re = new ArrayList<ImagePath>();

           ImagePathExample imagepathExample = new ImagePathExample();
           re = imagePathMapper.selectByExample(imagepathExample);
           for (ImagePath value : re) {
               value.MakeStamp();
               imagepathDA.saveImagePath(value);

               redisu.sAddAndTime("ImagePath_n"+value.getGoodid(), 0, value.getPathid());
               redisu.hset(classname, value.getPathid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, ImagePathJob.class, null, job);          
           }
       }
   }
}
