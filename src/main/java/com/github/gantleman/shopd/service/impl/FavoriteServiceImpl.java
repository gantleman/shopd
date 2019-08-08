package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.FavoriteDA;
import com.github.gantleman.shopd.da.FavoriteUserDA;
import com.github.gantleman.shopd.dao.FavoriteMapper;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import com.github.gantleman.shopd.entity.FavoriteUser;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.jobs.FavoriteJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired(required = false)
    FavoriteMapper favoriteMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private FavoriteJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Favorite";

    private String classname_extra = "Favorite_User";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, FavoriteJob.class, null, job);
        }
    }

    @Override
    public Favorite getFavoriteByKey(Integer favoriteid, String url) {
        Favorite re = null;
        Integer pageId = cacheService.PageID(favoriteid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Favorite) redisu.hget(classname, favoriteid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/favoritepage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname, favoriteid.toString())) {
                //read redis
                re = (Favorite) redisu.hget(classname, favoriteid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public List<Favorite> selectFavByUser(Integer userID, String url) {
        List<Favorite> re = new ArrayList<Favorite>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("favorite_u"+userID.toString());
            if(ro != null){
                for (Object id : ro) {
                    Favorite r =  getFavoriteByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);                
            }
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/favoriteuserpage", userID);
            }else{
                RefreshUserDBD(userID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("favorite_u"+userID.toString());
                if(ro != null){
                    for (Object id : ro) {
                        Favorite r =  getFavoriteByKey((Integer)id, url);
                        if (r != null)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);                
                }
            }
        }
        return re;
    }

    @Override
    public Favorite selectFavByKey(Integer userid, Integer goodsid, String url) {
        List<Favorite> lf = selectFavByUser(userid, url);
        Favorite  r = null;
        for(Favorite f: lf){
            if(f.getGoodsid() == goodsid){
                r = f;
            }
        }
        return r;
    }

    public void insertSelective_extra(Favorite favorite) {
        //add to FavoriteUserDA
        RefreshUserDBD(favorite.getUserid(), false, false);
        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        FavoriteUser favoriteUser = favoriteUserDA.findFavoriteUserById(favorite.getUserid());
        if(favoriteUser == null){
            favoriteUser = new FavoriteUser();
        }
        favoriteUser.addFavoriteList(favorite.getFavoriteid());
        favoriteUserDA.saveFavoriteUser(favoriteUser);

        //Re-publish to redis
        redisu.sAdd("favorite_u" + favorite.getUserid().toString(), favorite.getFavoriteid()); 
    }

    @Override
    public void insertFavorite(Favorite favorite) {
        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        favorite.setFavoriteid(new Long(id).intValue());
        favorite.setStatus(CacheService.STATUS_INSERT);
        favoriteDA.saveFavorite(favorite);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, favorite.getFavoriteid().toString(), (Object)favorite, 0);

        insertSelective_extra(favorite);
    }

    public void deleteByPrimaryKey_extra(Favorite favorite) {
        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        FavoriteUser favoriteUser = favoriteUserDA.findFavoriteUserById(favorite.getUserid());

        if(favoriteUser != null){
            favoriteUser.removeFavoriteList(favorite.getFavoriteid());
            if(favoriteUser.getFavoriteSize() >= 1){
                //Re-publish to redis
                 redisu.setRemove("favorite_u" + favorite.getUserid().toString(), favorite.getFavoriteid());
            } else if(favoriteUser.getFavoriteSize() == 0){
                //list empty
                favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                //Re-publish to redis
                redisu.del("favorite_u" + favorite.getUserid().toString());
            }
            favoriteUserDA.saveFavoriteUser(favoriteUser);
            BDBEnvironmentManager.getMyEntityStore().sync();
        }
    }

    @Override
    public void deleteFavByKey(Integer userid, Integer goodsid) {
        RefreshUserDBD(userid, false, false);

        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        FavoriteUser favoriteUser = favoriteUserDA.findFavoriteUserById(userid);
        if(favoriteUser != null){
            List<Integer> favoriteList = new ArrayList<>();
            favoriteList = favoriteUser.getFavoriteList();
            for(Integer favoriteid: favoriteList){
                RefreshDBD(cacheService.PageID(favoriteid), false);

                BDBEnvironmentManager.getInstance();
                FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
                Favorite favorite = favoriteDA.findFavoriteById(favoriteid);
        
                if (favorite != null && favorite.getGoodsid() == goodsid)
                {
                    favorite.setStatus(CacheService.STATUS_DELETE);
                    favoriteDA.saveFavorite(favorite);

                    //Re-publish to redis
                    redisu.hdel(classname, favorite.getFavoriteid().toString(), 0);
                    
                    deleteByPrimaryKey_extra(favorite);
                }  
            }
        } 
    }

  
    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                FavoriteUser favoriteUser = favoriteUserDA.findFavoriteUserById(i);
                if(favoriteUser != null){
                    favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                    redisu.del("favorite_u"+favoriteUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (favoriteUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Favorite favorite = favoriteDA.findFavoriteById(i);
                if(favorite != null){
                    if(null ==  favorite.getStatus()) {
                        favoriteDA.removedFavoriteById(favorite.getFavoriteid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  favorite.getStatus() && 1 == favoriteMapper.deleteByPrimaryKey(favorite.getFavoriteid())) {
                        favoriteDA.removedFavoriteById(favorite.getFavoriteid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  favorite.getStatus()  && 1 == favoriteMapper.insert(favorite)) {
                        favoriteDA.removedFavoriteById(favorite.getFavoriteid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  favorite.getStatus() && 1 == favoriteMapper.updateByPrimaryKey(favorite)) {
                        favoriteDA.removedFavoriteById(favorite.getFavoriteid());
                    }
                    redisu.hdel(classname, favorite.getFavoriteid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (favoriteDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, FavoriteJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Favorite> re = new ArrayList<Favorite>();          
            FavoriteExample favoriteExample = new FavoriteExample();
            favoriteExample.or().andFavoriteidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andFavoriteidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = favoriteMapper.selectByExample(favoriteExample);
            for (Favorite value : re) {
                redisu.hset(classname, value.getFavoriteid().toString(), value);
                favoriteDA.saveFavorite(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Favorite r = favoriteDA.findFavoriteById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<Favorite> re = new ArrayList<Favorite>();          
            FavoriteExample favoriteExample = new FavoriteExample();
            favoriteExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = favoriteMapper.selectByExample(favoriteExample);
            for (Favorite value : re) {
                FavoriteUser favorite  = favoriteUserDA.findFavoriteUserById(value.getUserid());
                if(favorite == null){
                    favorite = new FavoriteUser();
                }
                
                redisu.sAdd("favorite_g"+value.getGoodsid().toString(), (Object)value.getFavoriteid());

                if(andAll){ 
                    RefreshDBD(cacheService.PageID(value.getFavoriteid()), refresRedis);
                }

                favoriteUserDA.saveFavoriteUser(favorite);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    FavoriteUser r = favoriteUserDA.findFavoriteUserById(i);
                    if(r!= null){
                        List<Integer> li = r.getFavoriteList();
                        for(Integer id: li){
                            redisu.sAdd("favorite_u"+r.getUserid().toString(), (Object)id); 
                        }
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
