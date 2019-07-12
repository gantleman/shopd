package com.github.gantleman.shopd.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gantleman.shopd.da.CacheDA;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.ServerConfig;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    @Autowired(required = false)
    private CacheMapper cacheMapper;

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private ServerConfig sc;

    @Value("${srping.cache.pagecount}")
    Integer pagecount;

    @Value("${srping.cache.page}")
    Integer page;

    @Autowired
    private RedisUtil redisu;

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
                ra = sqlra.get(0);
            }
        }

        Long ri =  null; 
        if (ra == null)
         return ri;

        ri = ra.getcIndex() + 1;
        ra.setcIndex(ri);
        cacheDA.saveCache(ra);
        return ri;
    }

    @Override
    public Boolean IsCache(String tablename, Integer pageID) {

        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        if (ra != null) {
            Map<Integer, Integer> ruserid = ra.getUserid();
            if (ruserid != null)
                if(ruserid.containsKey(pageID)){
                    Integer value = ruserid.get(pageID);
                    ruserid.put(pageID, value+1);
                    return true;
                }
        }
        return false;
    }

    @Override
    public Boolean IsCache(String tablename) {

        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        if (ra != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> PageOut(String tablename) {
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        List<Integer> re = new ArrayList<Integer>();
        if (ra != null) {
            Map<Integer, Integer> ListPage = ra.getUserid();

            if(ListPage.size() >= pagecount){
                Integer l = ListPage.size()-pagecount;
                for(Integer i = 0;i < l;i++){
                    Integer k=0,v=0; 
                    for(Map.Entry<Integer, Integer> entry : ListPage.entrySet()){
                        Integer redispageid = (Integer)redisu.hget(tablename+"pageid", i.toString());
                        Integer rank;
                        if(redispageid != null){
                            rank = entry.getValue() + redispageid;
                        }else{
                            rank = entry.getValue();
                        }
                        if(v <= rank){
                            k = entry.getKey();
                            v = rank;
                        }
                    }
                    re.add(k);
                    ListPage.remove(k);
                }
            }

            if(!re.isEmpty()){
                ra.setUserid(ListPage);
                cacheDA.saveCache(ra);
            }
        }
        return re;
    }

    @Override
    public Integer PageBegin(Integer pageID) {
        return pageID*page;
    }

    @Override
    public Integer PageEnd(Integer pageID) {
        return pageID*page+page-1;
    }

    @Override
    public Integer PageID(Integer ID) {
        return ID/page;
    }
    

}

