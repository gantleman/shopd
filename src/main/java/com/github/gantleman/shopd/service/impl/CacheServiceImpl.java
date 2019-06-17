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

            ///create time todo
            
            if(sqlra != null)
            {
                ///save
                cacheDA.saveCache(sqlra.get(0));

                ///set host
                Cache sqlhost = sqlra.get(0);
                sqlhost.setcHost(sc.getUrl());
                cacheMapper.updateByPrimaryKeySelective(sqlhost);
            }
        }
    }

    @Override
    public void timeTask(String tablename) {
        // read BDB clear out time recorde
    }

    @Override
    public void Archive(String tablename) {

    }
}

