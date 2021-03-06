package com.github.gantleman.shopd.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import com.github.gantleman.shopd.da.CacheDA;
import com.github.gantleman.shopd.dao.CacheMapper;
import com.github.gantleman.shopd.entity.Cache;
import com.github.gantleman.shopd.entity.CacheExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.HttpUtils;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.ServerConfig;
import com.github.gantleman.shopd.util.TimeUtils;

import org.quartz.Job;
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

    @Value("${srping.cache.pageamount}")
    Integer pageamount;

    @Value("${srping.cache.pagesize}")
    Integer pagesize;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private HttpUtils httputils;

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
    public long EventCteate(String tablename){
        ///create new
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        if(ra == null)
        {
            CacheExample ce = new CacheExample();
            ce.or().andCNameEqualTo(tablename);
            List<Cache> sqlra = cacheMapper.selectByExample(ce);

            if(sqlra != null) {
                ///set host
                Cache sqlhost = sqlra.get(0);

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

        ri = ra.getcIndex() ;
        ra.setcIndex(ri+ 1);
        cacheDA.saveCache(ra);
        return ri;
    }

    @Override
    public Boolean IsCache(String tablename, Integer pageID, String classname, Class jobClass, Job job) {

        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        boolean re = false;

        if(ra == null ){
            CacheExample cacheExample = new CacheExample();
            cacheExample.or().andCNameEqualTo(tablename);
            List<Cache> lc = cacheMapper.selectByExample(cacheExample);

            if(!lc.isEmpty())
               ra = lc.get(0);

            quartzManager.addJob(classname,classname,classname,classname, jobClass, null, job);
        }
        
        Map<Integer, Integer> ruserid = ra.getUserid();
        if (ruserid == null)
            ruserid = new HashMap<Integer, Integer>();

        if(ruserid.containsKey(pageID)){
            Integer value = ruserid.get(pageID);
            ruserid.put(pageID, value+1);
            re = false;
        }else{
            ruserid.put(pageID, 1);
            re = true;
        }

        ra.setUserid(ruserid);
        cacheDA.saveCache(ra);
        return re;
    }

    @Override
    public Boolean IsCache(String tablename, Integer pageID) {

        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        boolean re = false;
        
        if(ra == null ){
            CacheExample cacheExample = new CacheExample();
            cacheExample.or().andCNameEqualTo(tablename);
            List<Cache> lc = cacheMapper.selectByExample(cacheExample);

            if(!lc.isEmpty())
               ra = lc.get(0);
        }

        Map<Integer, Integer> ruserid = ra.getUserid();
        if (ruserid == null)
            ruserid = new HashMap<Integer, Integer>();

        if(ruserid.containsKey(pageID)){
            Integer value = ruserid.get(pageID);
            ruserid.put(pageID, value+1);
            re = false;
        }else{
            ruserid.put(pageID, 1);
            re = true;
        }

        ra.setUserid(ruserid);
        cacheDA.saveCache(ra);
        return re;
    }

    @Override
    public Boolean IsCache(String tablename) {

        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        if (ra == null)
            return false;
        else
            return true;
    }

    @Override
    public List<Integer> PageOut(String tablename) {
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        List<Integer> re = new ArrayList<Integer>();
        if (ra != null) {
            Map<Integer, Integer> ListPage = ra.getUserid();

            if(ListPage.size() >= pageamount){
                Integer l = ListPage.size()- pageamount;
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
    public List<Integer> PageGetAll(String tablename) {
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        List<Integer> re = new ArrayList<Integer>();
        if (ra != null) {
            Map<Integer, Integer> ListPage = ra.getUserid();

            for(Map.Entry<Integer, Integer> entry : ListPage.entrySet()){
                re.add(entry.getKey());
            }
        }
        return re;
    }

    @Override
    public Integer PageBegin(Integer pageID) {
        return pageID*pagesize;
    }

    @Override
    public Integer PageEnd(Integer pageID) {
        return pageID*pagesize+pagesize-1;
    }

    @Override
    public Integer PageID(Integer ID) {
        return ID/pagesize;
    }

    @Override
    public Integer PageSize() {
        return pagesize;
    }
    
    @Override
    public boolean IsLocal(String url) {
        String host = (String)redisu.hget("routeconfig", url);
        if(host == null || host.equals(serverConfig.getUrl()))
        return true;
        else
        return false;
    }

    @Override
    public void RemoteRefresh(String url, Integer Id) {
        Map<String, String> headers = new HashMap<>(); 
        Map<String, String> querys = new HashMap<>();                
        querys.put("id", Id.toString());
        String host = (String)redisu.hget("routeconfig", url);
        try {
            if(host == null)
                throw new RuntimeException("Value for condition cannot be null");
            httputils.doGet(host, url, headers, querys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RemoteRefresh(String url, String name) {
        Map<String, String> headers = new HashMap<>(); 
        Map<String, String> querys = new HashMap<>();                
        querys.put("name", name);
        String host = (String)redisu.hget("routeconfig", url);
        try {
            if(host == null)
                throw new RuntimeException("Value for condition cannot be null");
            httputils.doGet(host, url, headers, querys);
        } catch (Exception e) {
            e.printStackTrace();
        }      
    }

    @Override
    public boolean EventCteateLocalCache(String tablename) {
        ///create new
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);
        if(ra == null)
        {
            CRC32 crc = new CRC32();
            crc.update(tablename.getBytes());
            
            ra = new Cache();
            Long id =crc.getValue();
            ra.setcId(id.intValue());
            ra.setcName(tablename);
            cacheDA.saveCache(ra);
            return true;
        }
        return false;
    }

    @Override
    public void ArchiveLocalCache(String tablename) {
        //backe to sql
        BDBEnvironmentManager.getInstance();
        CacheDA cacheDA=new CacheDA(BDBEnvironmentManager.getMyEntityStore());
        Cache ra = cacheDA.findCacheByName(tablename);

        if(ra != null)
        {
            cacheDA.removedCacheById(ra.getcId());
        }
    }
}

