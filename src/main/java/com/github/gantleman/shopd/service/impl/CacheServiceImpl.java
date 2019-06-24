package com.github.gantleman.shopd.service.impl;


import java.util.List;

import com.github.gantleman.shopd.da.CacheDA;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.ServerConfig;
import com.github.gantleman.shopd.util.TimeUtils;

import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    @Autowired(required = false)
    private CacheMapper cacheMapper;

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private ServerConfig sc;

    ///time one hour clean cahce
    @Override
    public void eventAdd(String tablename){
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        if(ra == null)
        {
            CacheExample ce = new CacheExample();;
            ce.or().andCNameEqualTo(tablename);
            List<Cache> sqlra = cacheMapper.selectByExample(ce);

            if(sqlra != null)
            {
                ///set host
                Cache sqlhost = sqlra.get(0);
                if((sqlhost.getcHost()!=null&&sqlhost.getcHost2()!=null)&&(!sqlhost.getcHost().equals(sc.getUrl()) && !sqlhost.getcHost().equals(sqlhost.getcHost2()))) {

                } 
                sqlhost.setcHost(sc.getUrl());
                sqlhost.setcStamp(TimeUtils.getTimeWhitLong());
                cacheMapper.updateByPrimaryKeySelective(sqlhost);
                
                ///save
                cacheDA.saveCache(sqlra.get(0));
            }
        }
    }

    @Override
    public void Archive(String tablename) {
        //backe to sql
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        if(ra != null)
        {
            ra.setcHost2(sc.getUrl());
            ra.setcStamp2(TimeUtils.getTimeWhitLong());
            cacheMapper.updateByPrimaryKeySelective(ra);

            quartzManager.removeJob(tablename,tablename, tablename, tablename);

            cacheDA.removedCacheById(ra.getcId());
        }
    }

    @Override
    public long eventCteate(String tablename){
        ///create new
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        if(ra == null)
        {
            CacheExample ce = new CacheExample();;
            ce.or().andCNameEqualTo(tablename);
            List<Cache> sqlra = cacheMapper.selectByExample(ce);

            if(sqlra != null)
            {
                ///set host
                Cache sqlhost = sqlra.get(0);
                if(!sqlhost.getcHost().equals(sc.getUrl()) && !sqlhost.getcHost().equals(sqlhost.getcHost2())) {

                } 

                sqlhost.setcIndex(0L);
                sqlhost.setcHost(sc.getUrl());
                sqlhost.setcStamp(TimeUtils.getTimeWhitLong());
                cacheMapper.updateByPrimaryKeySelective(sqlhost);
                
                ///save
                cacheDA.saveCache(sqlra.get(0));
            }
        }

        Long ri = ra.getcIndex() + 1;
        ra.setcIndex(ri);
        cacheDA.saveCache(ra);

        return ri;
    }
}

