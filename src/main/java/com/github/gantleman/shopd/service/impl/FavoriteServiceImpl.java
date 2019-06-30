package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.FavoriteDA;
import com.github.gantleman.shopd.dao.FavoriteMapper;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import com.github.gantleman.shopd.entity.FavoriteKey;
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

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, FavoriteJob.class, null, job);
        }
    }

    @Override
    public void insertFavorite(Favorite favorite) {
        favoriteMapper.insertSelective(favorite);
    }

    @Override
    public Favorite selectFavByKey(FavoriteKey favoriteKey) {
        return favoriteMapper.selectByPrimaryKey(favoriteKey);
    }

    @Override
    public void deleteFavByKey(FavoriteKey favoriteKey) {
        favoriteMapper.deleteByPrimaryKey(favoriteKey);
    }

    @Override
    public List<Favorite> selectFavByExample(Integer userid) {

        FavoriteExample favoriteExample = new FavoriteExample();
        favoriteExample.or().andUseridEqualTo(userid);

        return favoriteMapper.selectByExample(favoriteExample);
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
        List<Favorite> lfavorite = favoriteDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Favorite favorite : lfavorite) {
            if(null ==  favorite.getStatus()) {
                favoriteDA.removedFavoriteById(favorite.getGoodsid());
            }

            if(2 ==  favorite.getStatus()  && 1 == favoriteMapper.insert(favorite)) {
                favoriteDA.removedFavoriteById(favorite.getGoodsid());
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
           re = favoriteMapper.selectByExample(favoriteExample);
           for (Favorite value : re) {
               value.MakeStamp();
               favoriteDA.saveFavorite(value);

               redisu.sAddAndTime("Favorite_u"+userid.toString(), 0, value.getGoodsid()); 
               redisu.hset(classname, value.getGoodsid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();

           id.add(userid);
           cacheService.eventAdd(classname, id);
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, FavoriteJob.class, null, job);          
           }
       }
   }
}
