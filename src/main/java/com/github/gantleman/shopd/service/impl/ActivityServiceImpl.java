package com.github.gantleman.shopd.service.impl;

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
import com.github.gantleman.shopd.util.TimeUtils;

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
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

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
    public List<Activity> getAllActivity() {
        List<Activity> re = new ArrayList<Activity>();

        if(redisu.hasKey(classname)) {
            //read redis
            Map<Object, Object> rm = redisu.hmget(classname);
            for (Object value : rm.values()) {
                re.add( (Activity)value);
            }

            redisu.expire(classname, 0);
        }else {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            re = activityMapper.selectByExample(new ActivityExample());
            for (Activity value : re) {
                tmap.put(value.getActivityid().toString(), (Object)value);
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }
        return re;
    }

    @Override
    public Activity selectByKey(Integer activityid) {
        Activity re = null;
        if(redisu.hHasKey(classname, activityid.toString())) {
            //read redis
            Object o = redisu.hget(classname, activityid.toString());
            re = (Activity) o;

            redisu.expire(classname, 0);
        }else {
            ///init only one
            if(!redisu.hasKey(classname)) {
                //write redis
                Map<String, Object> tmap = new HashMap<>();
                List<Activity> lreturn = new ArrayList<Activity>();

                lreturn = activityMapper.selectByExample(new ActivityExample());
                for (Activity value : lreturn) {
                    tmap.put(value.getActivityid().toString(), (Object)value);
                    if(value.getActivityid() == activityid){
                        re = value;
                    }
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
    public void deleteByActivityId(Integer activityid) {

        RefreshDBD();

       BDBEnvironmentManager.getInstance();
       ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());
       Activity activity = activityDA.findActivityById(activityid);

       if (activity != null)
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

        RefreshDBD();

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
        List<Activity> lactivity = activityDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Activity activity : lactivity) {
            if(null ==  activity.getStatus()) {
                activityDA.removedActivityById(activity.getActivityid());
            }

            if(1 ==  activity.getStatus() && 1 == activityMapper.deleteByPrimaryKey(activity.getActivityid())) {
                activityDA.removedActivityById(activity.getActivityid());
            }

            if(2 ==  activity.getStatus()  && 1 == activityMapper.insert(activity)) {
                activityDA.removedActivityById(activity.getActivityid());
            }
        }

        if (activityDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        BDBEnvironmentManager.getInstance();
        ActivityDA activityDA=new ActivityDA(BDBEnvironmentManager.getMyEntityStore());

        if (cacheService.IsCache(classname)) {
            ///init
            List<Activity> re = new ArrayList<Activity>();
            Map<String, Object> tmap = new HashMap<>();

            re = activityMapper.selectByExample(new ActivityExample());
            for (Activity value : re) {
                tmap.put(value.getActivityid().toString(), (Object)value);

                value.MakeStamp();
                activityDA.saveActivity(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            quartzManager.addJob(classname,classname,classname,classname, ActivityJob.class, null, job);

            redisu.hmset(classname, tmap, 0);
        }        
    }
}
