package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.ActivityMapper;
import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.entity.ActivityExample;
import com.github.gantleman.shopd.service.ActivityService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService,Job {

    @Autowired(required = false)
    ActivityMapper activityMapper;

    @Override
    public List<Activity> getAllActivity() {
        return activityMapper.selectByExample(new ActivityExample());
    }

    @Override
    public void insertActivitySelective(Activity activity) {
        activityMapper.insertSelective(activity);
    }

    @Override
    public Activity selectByKey(Integer activityid) {
        return activityMapper.selectByPrimaryKey(activityid);
    }

    @Override
    public void deleteByActivityId(Integer activityid) {
        activityMapper.deleteByPrimaryKey(activityid);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
