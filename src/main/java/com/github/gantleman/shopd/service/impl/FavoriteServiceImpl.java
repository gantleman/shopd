package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.FavoriteDA;
import com.github.gantleman.shopd.dao.FavoriteMapper;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.jobs.FavoriteJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    FavoriteMapper favoriteMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private FavoriteJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Favorite";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, FavoriteJob.class, null, job);
        }
    }

    @Override
    public Favorite selectFavByKey(Integer userid, Integer goodsid) {
        Favorite re = null;
        if(redisu.hasKey("Favorite_u"+userid.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Favorite_u"+userid.toString());
            for (Object id : ro) {
                if(id == goodsid) {
                    re =  (Favorite) redisu.hget(classname, ((Integer)id).toString());
                }
            }
            redisu.expire("Favorite_u"+goodsid.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            FavoriteExample favoriteExample = new FavoriteExample();
            favoriteExample.or().andUseridEqualTo(userid);            
            List<Favorite> lre = favoriteMapper.selectByExample(favoriteExample);

            ///read and write
            if(!redisu.hasKey("Favorite_u"+userid.toString())) {
                for( Favorite item : lre ){
                    redisu.sAdd("Favorite_u"+userid.toString(), (Object)item.getFavoriteid());
                    redisu.hset(classname, item.getFavoriteid().toString(), item);

                    if (re != null && item != null && item.getGoodsid() == goodsid)
                        re = item;
                }
                redisu.expire("Favorite_u"+userid.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Favorite> selectFavByUser(Integer userid) {

        List<Favorite> re = new ArrayList<Favorite>();
        if(redisu.hasKey("Favorite_u"+userid.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Favorite_u"+userid.toString());
            for (Object id : ro) {
                
                Favorite r =  (Favorite) redisu.hget(classname, ((Integer)id).toString());
                if(r != null){
                    re.add(r);
                } 
            }
            redisu.expire("Favorite_u"+userid.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            FavoriteExample favoriteExample = new FavoriteExample();
            favoriteExample.or().andUseridEqualTo(userid);            
            re = favoriteMapper.selectByExample(favoriteExample);

            ///read and write
            if(!redisu.hasKey("Favorite_u"+userid.toString())) {
                for( Favorite item : re ){
                    redisu.sAdd("Favorite_u"+userid.toString(), (Object)item.getFavoriteid());
                    redisu.hset(classname, item.getFavoriteid().toString(), item);
                }
                redisu.expire("Favorite_u"+userid.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public void insertFavorite(Favorite favorite) {
        RefreshDBD(favorite.getUserid());

        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        favorite.setFavoriteid(new Long(id).intValue());
        favorite.MakeStamp();
        favorite.setStatus(2);
        favoriteDA.saveFavorite(favorite);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Favorite_u" + favorite.getUserid().toString(), 0, favorite.getFavoriteid()); 
        redisu.hset(classname, favorite.getFavoriteid().toString(), (Object)favorite, 0);
    }

    @Override
    public void deleteFavByKey(Integer userid, Integer goodsid) {
        RefreshDBD(userid);

        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
        List<Favorite> favorite = favoriteDA.findAllFavoriteByUserID(userid);
 
        if (!favorite.isEmpty())
        {
            for(Favorite fa:favorite) {
                if(fa.getGoodsid() == goodsid){
                    fa.MakeStamp();
                    fa.setStatus(1);
                    favoriteDA.saveFavorite(fa);
        
                    //Re-publish to redis
                    redisu.hdel(classname, fa.getFavoriteid().toString());
                }
            }
        }
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
        List<Favorite> lfavorite = favoriteDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Favorite favorite : lfavorite) {
            if(null ==  favorite.getStatus()) {
                favoriteDA.removedFavoriteById(favorite.getFavoriteid());
            }

            if(1 ==  favorite.getStatus() && 1 == favoriteMapper.deleteByPrimaryKey(favorite.getFavoriteid())) {
                favoriteDA.removedFavoriteById(favorite.getFavoriteid());
            }

            if(2 ==  favorite.getStatus()  && 1 == favoriteMapper.insert(favorite)) {
                favoriteDA.removedFavoriteById(favorite.getFavoriteid());
            }
        }

        if (favoriteDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD(Integer userid) {
        ///init
       if (cacheService.IsCache(classname, userid)) {
           BDBEnvironmentManager.getInstance();
           FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<Favorite> re = new ArrayList<Favorite>();

           FavoriteExample favoriteExample = new FavoriteExample();
           favoriteExample.or().andUseridEqualTo(userid);
           re = favoriteMapper.selectByExample(favoriteExample);
           for (Favorite value : re) {
               value.MakeStamp();
               favoriteDA.saveFavorite(value);

               redisu.sAddAndTime("Favorite_u"+userid.toString(), 0, value.getGoodsid()); 
               redisu.hset(classname, value.getGoodsid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, FavoriteJob.class, null, job);          
           }
       }
   }
}
