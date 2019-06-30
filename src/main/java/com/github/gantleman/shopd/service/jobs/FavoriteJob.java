package com.github.gantleman.shopd.service.jobs;

import com.github.gantleman.shopd.util.HttpUtils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoriteJob implements Job {
  
    @Autowired
    HttpUtils httputils;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            httputils.doGet("/favoritetick");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
	}

}