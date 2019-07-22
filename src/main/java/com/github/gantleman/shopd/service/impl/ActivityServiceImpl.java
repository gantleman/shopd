package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.ActivityDA;
import com.github.gantleman.shopd.dao.ActivityMapper;
import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.entity.ActivityExample;
import com.github.gantleman.shopd.service.ActivityService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.ActivityJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    @Autowired(required = false)
    ActivityMapper activityMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ActivityJob job;
    
    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Activity";
    
    @PostConstruct
    public void init() {
      if (cacheService.IsCache(classname)) {
            ///create time
           quartzManager.addJob(classname,classname,classname,classname, ActivityJob.class, null, job);
        }
    }

    @Override
    public List<Activity> getAllActivity(Integer pageId, String url) {
        List<Activity> re = new ArrayList<Activity>();

        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Activity r = (Activity) redisu.hget(classname, pageId.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/activitypage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Activity r = (Activity) redisu.hget(classname, i.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }
        return re;
    }

    @Override
    public Activity selectByKey(Integer activityid, String url) {
        Activity re = null;
        Integer pageId = cacheService.PageID(activityid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Object o = redisu.hget(classname, activityid.toString());
            if(o != null){
                re = (Activity) o;    
            }
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/activitypage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }
            
            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                Object o = redisu.hget(classname, activityid.toString());
                if(o != null){
                    re = (Activity) o;    
                }
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public void deleteByActivityId(Integer activityid) {
        RefreshDBD(cacheService.PageID(activityid), false);

       BDBEnvironmentManager.getInstance();
       ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());
       Activity activity = activityDA.findActivityById(activityid);

       if(activity != null && activity.getStatus() == CacheService.STATUS_INSERT){
            activityDA.removedActivityById(activityid);
            //Re-publish to redis
            redisu.hdel(classname, activity.getActivityid().toString());
       } else if (activity != null)
       {
            activity.setStatus(CacheService.STATUS_DELETE);
            activityDA.saveActivity(activity);

            //Re-publish to redis
            redisu.hdel(classname, activity.getActivityid().toString());
       } 
    }

    @Override
    public void insertActivitySelective(Activity activity) {

        RefreshDBD(cacheService.PageID(activity.getActivityid()), false);

        BDBEnvironmentManager.getInstance();
        ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        activity.setActivityid(new Long(id).intValue());
        activity.setStatus(CacheService.STATUS_INSERT);
        activityDA.saveActivity(activity);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, activity.getActivityid().toString(), activity, 0);
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Activity activity = activityDA.findActivityById(i);
                if(activity != null){
                    if(null ==  activity.getStatus()) {
                        activityDA.removedActivityById(activity.getActivityid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  activity.getStatus() && 1 == activityMapper.deleteByPrimaryKey(activity.getActivityid())) {
                        activityDA.removedActivityById(activity.getActivityid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  activity.getStatus()  && 1 == activityMapper.insert(activity)) {
                        activityDA.removedActivityById(activity.getActivityid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  activity.getStatus() && 1 == activityMapper.updateByPrimaryKey(activity)) {
                        activityDA.removedActivityById(activity.getActivityid());
                    } 
                    
                    redisu.hdel(classname, activity.getActivityid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (activityDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, ActivityJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Activity> re = new ArrayList<Activity>();          
            ActivityExample activityExample = new ActivityExample();
            activityExample.or().andActivityidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            activityExample.or().andActivityidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = activityMapper.selectByExample(activityExample);
            for (Activity value : re) {
                redisu.hset(classname, value.getActivityid().toString(), value);
                activityDA.saveActivity(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){

                    Activity r = activityDA.findActivityById(i);
                    if(r != null && r.getStatus() != CacheService.STATUS_DELETE)
                     redisu.hset(classname, i.toString(), r);                        
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }
}
