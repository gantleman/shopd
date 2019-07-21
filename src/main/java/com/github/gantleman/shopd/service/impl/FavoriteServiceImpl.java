package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.FavoriteDA;
import com.github.gantleman.shopd.da.FavoriteUserDA;
import com.github.gantleman.shopd.dao.FavoriteMapper;
import com.github.gantleman.shopd.dao.FavoriteUserMapper;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import com.github.gantleman.shopd.entity.FavoriteUser;
import com.github.gantleman.shopd.entity.FavoriteUserExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.jobs.FavoriteJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired(required = false)
    FavoriteMapper favoriteMapper;

    @Autowired(required = false)
    private FavoriteUserMapper favoriteUserMapper;

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

        if(redisu.hasKey("favorite_u"+userID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("favorite_u"+userID.toString());
            for (Object id : ro) {
                Favorite r =  getFavoriteByKey((Integer)id, url);
                if (r != null)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/favoriteuserpage", userID);
            }else{
                RefreshUserDBD(userID, true, true);
            }

            if(redisu.hasKey("favorite_u"+userID.toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("favorite_u"+userID.toString());
                re = new ArrayList<Favorite>();
                for (Object id : ro) {
                    Favorite r =  getFavoriteByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
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
            List<Integer> favoriteIdList = new ArrayList<>();
            favoriteIdList.add(favorite.getFavoriteid());
            JSONArray jsonArray = JSONArray.fromObject(favoriteIdList);

            favoriteUser = new FavoriteUser();
            favoriteUser.setFavoriteSize(1); 
            favoriteUser.setFavoriteList(jsonArray.toString());
            favoriteUser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> favoriteIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(favoriteUser.getFavoriteList());
            favoriteIdList = JSONArray.toList(jsonArray,Integer.class);
            favoriteIdList.add(favorite.getFavoriteid());

            favoriteUser.setFavoriteSize(favoriteUser.getFavoriteSize() + 1); 
            favoriteUser.setFavoriteList(jsonArray.toString());
            if(favoriteUser.getStatus() == null || favoriteUser.getStatus() == CacheService.STATUS_DELETE)
                favoriteUser.setStatus(CacheService.STATUS_UPDATE);
        }
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
            List<Integer> favoriteList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(favoriteUser.getFavoriteList());
            favoriteList = JSONArray.toList(jsonArray, Integer.class);

            favoriteList.remove(favorite.getFavoriteid());
            jsonArray = JSONArray.fromObject(favoriteList);
            favoriteUser.setFavoriteList(jsonArray.toString());

            if(favoriteUser.getFavoriteSize() >= 1){
                favoriteUser.setFavoriteSize(favoriteUser.getFavoriteSize() - 1);
                //Re-publish to redis
                 redisu.setRemove("favorite_u" + favorite.getUserid().toString(), favorite.getFavoriteid());
            } else if(favoriteUser.getFavoriteSize() == 0){
                //list empty
                if(favoriteUser.getStatus() == CacheService.STATUS_INSERT){
                    favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                }else{
                    favoriteUser.setStatus(CacheService.STATUS_DELETE);
                }
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
            JSONArray jsonArray = JSONArray.fromObject(favoriteUser.getFavoriteList());
            favoriteList = JSONArray.toList(jsonArray, Integer.class);

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
    public void TickBack_extra() {
        BDBEnvironmentManager.getInstance();
        FavoriteUserDA favoriteUserDA=new FavoriteUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                FavoriteUser favoriteUser = favoriteUserDA.findFavoriteUserById(i);
                if(favoriteUser != null){
                    if(null ==  favoriteUser.getStatus()) {
                        favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  favoriteUser.getStatus() && 1 == favoriteUserMapper.deleteByPrimaryKey(favoriteUser.getUserid())) {
                        favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  favoriteUser.getStatus()  && 1 == favoriteUserMapper.insert(favoriteUser)) {
                        favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  favoriteUser.getStatus() && 1 == favoriteUserMapper.updateByPrimaryKey(favoriteUser)) {
                        favoriteUserDA.removedFavoriteUserById(favoriteUser.getUserid());
                    }
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
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
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

        TickBack_extra();
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, FavoriteJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            FavoriteDA favoriteDA=new FavoriteDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Favorite> re = new ArrayList<Favorite>();          
            FavoriteExample favoriteExample = new FavoriteExample();
            favoriteExample.or().andFavoriteidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            favoriteExample.or().andFavoriteidLessThanOrEqualTo(cacheService.PageEnd(pageID));

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
            List<FavoriteUser> re = new ArrayList<FavoriteUser>();          
            FavoriteUserExample favoriteUserExample = new FavoriteUserExample();
            favoriteUserExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)));
            favoriteUserExample.or().andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = favoriteUserMapper.selectByExample(favoriteUserExample);
            for (FavoriteUser value : re) {
                favoriteUserDA.saveFavoriteUser(value);

                List<Integer> favoriteIdList = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(value.getFavoriteList());
                favoriteIdList = JSONArray.toList(jsonArray, Integer.class);

                for(Integer favoriteId: favoriteIdList){
                    redisu.sAdd("favorite_u"+value.getUserid().toString(), (Object)favoriteId);
                }

                if(andAll && userID == value.getUserid() && value.getFavoriteSize() != 0){  
                    for(Integer favoriteId: favoriteIdList){
                        RefreshDBD(cacheService.PageID(favoriteId), refresRedis);
                    }
                }
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    FavoriteUser r = favoriteUserDA.findFavoriteUserById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.sAdd("favorite_u"+r.getUserid().toString(), (Object)r.getFavoriteList()); 
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
