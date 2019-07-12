package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.ActivityDA;
import com.github.gantleman.shopd.dao.ActivityMapper;
import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.entity.ActivityExample;
import com.github.gantleman.shopd.service.ActivityService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.jobs.ActivityJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.HttpUtils;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.ServerConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    @Autowired(required = false)
    ActivityMapper activityMapper;

    @Autowired(required = false)
    ServerConfig serverConfig;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ActivityJob job;

    @Autowired
    HttpUtils httputils;
    
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
            String host = (String)redisu.get(url);
            if(!host.equals(serverConfig.getUrl())){
                Map<String, String> headers = new HashMap(); 
                Map<String, String> querys = new HashMap();

                querys.put("pageid", pageId.toString());

                try {
                    httputils.doGet(serverConfig.getUrl(), "/activitypage", headers, querys);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        if(redisu.hHasKey(classname, activityid.toString())) {
            //read redis
            Object o = redisu.hget(classname, activityid.toString());
            if(o != null){
                re = (Activity) o;
                Integer pageId = cacheService.PageID(activityid);
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }else {
            String host = (String)redisu.get(url);
            Integer pageId = cacheService.PageID(activityid);
            if(!host.equals(serverConfig.getUrl())){
                Map<String, String> headers = new HashMap(); 
                Map<String, String> querys = new HashMap();                
                querys.put("pageid", pageId.toString());

                try {
                    httputils.doGet(serverConfig.getUrl(), "/activitypage", headers, querys);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                RefreshDBD(pageId, true);
            }
            Object o = redisu.hget(classname, activityid.toString());
            if(o != null){
                re = (Activity) o;
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

       if(activity != null && activity.getStatus() == 2){
            activityDA.removedActivityById(activityid);
            //Re-publish to redis
            redisu.hdel(classname, activity.getActivityid().toString());
       } else if (activity != null)
       {
            activity.MakeStamp();
            activity.setStatus(1);
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

        long id = cacheService.eventCteate(classname);
        activity.setActivityid(new Long(id).intValue());
        activity.MakeStamp();
        activity.setStatus(2);
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
        
                    if(1 ==  activity.getStatus() && 1 == activityMapper.deleteByPrimaryKey(activity.getActivityid())) {
                        activityDA.removedActivityById(activity.getActivityid());
                    }
        
                    if(2 ==  activity.getStatus()  && 1 == activityMapper.insert(activity)) {
                        activityDA.removedActivityById(activity.getActivityid());
                    } 

                    if(3 ==  activity.getStatus() && 1 == activityMapper.updateByPrimaryKey(activity)) {
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

    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (cacheService.IsCache(classname, pageID)) {
            BDBEnvironmentManager.getInstance();
            ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Activity> re = new ArrayList<Activity>();          
            ActivityExample zctivityExample = new ActivityExample();
            zctivityExample.or().andActivityidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            zctivityExample.or().andActivityidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = activityMapper.selectByExample(zctivityExample);
            for (Activity value : re) {
                redisu.hset(classname, value.getActivityid().toString(), value);

                value.MakeStamp();
                activityDA.saveActivity(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            quartzManager.addJob(classname,classname,classname,classname, ActivityJob.class, null, job);
        }else if(refresRedis){
            if(redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){

                    Activity r = activityDA.findActivityById(i);
                    if(r!= null && r.getStatus() != 1)
                     redisu.hset(classname, i.toString(), r);                        
                }
            }
        }    
    }
}
