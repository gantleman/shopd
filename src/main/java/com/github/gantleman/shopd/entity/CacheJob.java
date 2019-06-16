package com.github.gantleman.shopd.entity;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CacheJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new Date() + ": blank job doing something...");
    }

    public void doNothing(){
        System.out.println(new Date() + ": blank job doing nothing...");
    }
}