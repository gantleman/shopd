package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.ShopCartDA;
import com.github.gantleman.shopd.da.ShopcartUserDA;
import com.github.gantleman.shopd.dao.ShopCartMapper;
import com.github.gantleman.shopd.dao.ShopcartUserMapper;
import com.github.gantleman.shopd.entity.ShopCart;
import com.github.gantleman.shopd.entity.ShopCartExample;
import com.github.gantleman.shopd.entity.ShopcartUser;
import com.github.gantleman.shopd.entity.ShopcartUserExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ShopCartService;
import com.github.gantleman.shopd.service.jobs.ShopCartJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service("addShopCart")
public class ShopCartServiceImpl implements ShopCartService {

    @Autowired(required = false)
    ShopCartMapper shopCartMapper;

    @Autowired(required = false)
    private ShopcartUserMapper shopcartUserMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ShopCartJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "ShopCart";

    private String classname_extra = "ShopCart_User";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, ShopCartJob.class, null, job);
        }
    }

    @Override
    public List<ShopCart> selectByID(Integer userID, String url) {
        List<ShopCart> re = new  ArrayList<ShopCart>();
        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("shopcart_u"+userID.toString());
            if(ro != null) {
                for (Object id : ro) {
                    ShopCart r =  getShopCartByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);                
            }
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/shopcartuserpage", userID);
            }else{
                RefreshUserDBD(userID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("shopcart_u"+userID.toString());
                if(ro != null) {
                    for (Object id : ro) {
                        ShopCart r =  getShopCartByKey((Integer)id, url);
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
    public ShopCart getShopCartByKey(Integer shopcartid, String url) {
        ShopCart re = null;
        Integer pageId = cacheService.PageID(shopcartid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (ShopCart) redisu.hget(classname, shopcartid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/shopcartpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }
            
            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                re = (ShopCart) redisu.hget(classname, shopcartid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    public void insertSelective_extra(ShopCart shopcart) {
        //add to ShopcartUserDA
        RefreshUserDBD(shopcart.getUserid(), false, false);
        BDBEnvironmentManager.getInstance();
        ShopcartUserDA shopcartUserDA=new ShopcartUserDA(BDBEnvironmentManager.getMyEntityStore());
        ShopcartUser shopcartUser = shopcartUserDA.findShopcartUserById(shopcart.getUserid());
        if(shopcartUser == null){
            List<Integer> shopcartIdList = new ArrayList<>();
            shopcartIdList.add(shopcart.getShopcartid());
            JSONArray jsonArray = JSONArray.fromObject(shopcartIdList);

            shopcartUser = new ShopcartUser();
            shopcartUser.setShopcartSize(1); 
            shopcartUser.setShopcartList(jsonArray.toString());
            shopcartUser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> shopcartIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(shopcartUser.getShopcartList());
            shopcartIdList = JSONArray.toList(jsonArray,Integer.class);
            shopcartIdList.add(shopcart.getShopcartid());

            shopcartUser.setShopcartSize(shopcartUser.getShopcartSize() + 1); 
            shopcartUser.setShopcartList(jsonArray.toString());
            if(shopcartUser.getStatus() == null || shopcartUser.getStatus() == CacheService.STATUS_DELETE)
                shopcartUser.setStatus(CacheService.STATUS_UPDATE);
        }
        shopcartUserDA.saveShopcartUser(shopcartUser);

        //Re-publish to redis
        redisu.sAdd("shopcart_u" + shopcart.getUserid().toString(), shopcart.getShopcartid()); 
    }

    @Override
    public void addShopCart(ShopCart shopcart) {
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        shopcart.setShopcartid(new Long(id).intValue());
        shopcart.setStatus(CacheService.STATUS_INSERT);
        shopcartDA.saveShopCart(shopcart);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, shopcart.getShopcartid().toString(), (Object)shopcart, 0);

        insertSelective_extra(shopcart);
    }

    public void deleteByPrimaryKey_extra(ShopCart shopcart) {
        RefreshUserDBD(shopcart.getUserid(), false, false);

        BDBEnvironmentManager.getInstance();
        ShopcartUserDA shopcartUserDA=new ShopcartUserDA(BDBEnvironmentManager.getMyEntityStore());
        ShopcartUser shopcartUser = shopcartUserDA.findShopcartUserById(shopcart.getUserid());

        if(shopcartUser != null){
            List<Integer> shopcartList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(shopcartUser.getShopcartList());
            shopcartList = JSONArray.toList(jsonArray, Integer.class);

            shopcartList.remove(shopcart.getShopcartid());
            JSONArray jsonarray = JSONArray.fromObject(shopcartList);
            shopcartUser.setShopcartList(jsonarray.toString());

            if(shopcartUser.getShopcartSize() >= 1){
                shopcartUser.setShopcartSize(shopcartUser.getShopcartSize() - 1);
                //Re-publish to redis
                 redisu.setRemove("shopcart_u" + shopcart.getUserid().toString(), shopcart.getShopcartid());
            } else if(shopcartUser.getShopcartSize() == 0){
                //list empty
                if(shopcartUser.getStatus() == CacheService.STATUS_INSERT){
                    shopcartUserDA.removedShopcartUserById(shopcartUser.getUserid());
                }else{
                    shopcartUser.setStatus(CacheService.STATUS_DELETE);
                }
                //Re-publish to redis
                redisu.del("shopcart_u" + shopcart.getUserid().toString());
            }
            shopcartUserDA.saveShopcartUser(shopcartUser);
            BDBEnvironmentManager.getMyEntityStore().sync();
        }
    }

    @Override
    public void deleteByPrimaryKey(Integer shopcartid) {
        RefreshDBD(cacheService.PageID(shopcartid), false);

        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        ShopCart shopcart = shopcartDA.findShopCartById(shopcartid);
 
        if (shopcart != null)
        {
             shopcart.setStatus(CacheService.STATUS_DELETE);
             shopcartDA.saveShopCart(shopcart);

             //Re-publish to redis
             redisu.hdel(classname, shopcart.getShopcartid().toString(), 0);
             
             deleteByPrimaryKey_extra(shopcart);
        }   
    }

    @Override
    public void deleteByKey(Integer userid, Integer goodsid) {
        ShopCart r =  selectCartByKey(userid, goodsid);
        if(r != null){
            deleteByPrimaryKey(r.getShopcartid());
        }
    }

    @Override
    public void updateCartByKey(ShopCart ishopcart) {
        RefreshDBD(ishopcart.getShopcartid(), false);

        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        ShopCart shopcart = shopcartDA.findShopCartById(ishopcart.getShopcartid());
 
        if (shopcart != null)
        {
            if(ishopcart.getStatus()== null)
                ishopcart.setStatus(CacheService.STATUS_UPDATE);
            
            if(ishopcart.getCatedate() != null){
                shopcart.setCatedate(ishopcart.getCatedate());
            }
            if(ishopcart.getGoodsid() != null){
                shopcart.setGoodsid(ishopcart.getGoodsid());
            }
            if(ishopcart.getGoodsnum() != null){
                shopcart.setGoodsnum(ishopcart.getGoodsnum());

            }
            if(ishopcart.getUserid() != null){
                shopcart.setUserid(ishopcart.getUserid());
            }
            shopcartDA.saveShopCart(shopcart);

            //Re-publish to redis
            redisu.sAddAndTime("ShopCart_u"+shopcart.getUserid().toString(), 0, (Object)shopcart.getShopcartid());
            redisu.hset(classname, shopcart.getShopcartid().toString(), (Object)shopcart, 0);
        }
    }

    @Override
    public ShopCart selectCartByKey(Integer userid, Integer goodsid) {
        RefreshUserDBD(userid, false, false);
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        List <ShopCart> shopCart = shopcartDA.findAllShopCartByUGID(userid, goodsid);

        if( shopCart.isEmpty())
        return null;
        else 
        return shopCart.get(0);
    }

    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ShopcartUserDA shopcartUserDA=new ShopcartUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ShopcartUser shopcartUser = shopcartUserDA.findShopcartUserById(i);
                if(shopcartUser != null){
                    if(null ==  shopcartUser.getStatus()) {
                        shopcartUserDA.removedShopcartUserById(shopcartUser.getUserid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  shopcartUser.getStatus() && 1 == shopcartUserMapper.deleteByPrimaryKey(shopcartUser.getUserid())) {
                        shopcartUserDA.removedShopcartUserById(shopcartUser.getUserid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  shopcartUser.getStatus()  && 1 == shopcartUserMapper.insert(shopcartUser)) {
                        shopcartUserDA.removedShopcartUserById(shopcartUser.getUserid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  shopcartUser.getStatus() && 1 == shopcartUserMapper.updateByPrimaryKey(shopcartUser)) {
                        shopcartUserDA.removedShopcartUserById(shopcartUser.getUserid());
                    }
                    redisu.del("shopcart_u"+shopcartUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (shopcartUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ShopCart shopcart = shopcartDA.findShopCartById(i);
                if(shopcart != null){
                    if(null ==  shopcart.getStatus()) {
                        shopcartDA.removedShopCartById(shopcart.getShopcartid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  shopcart.getStatus() && 1 == shopCartMapper.deleteByPrimaryKey(shopcart.getShopcartid())) {
                        shopcartDA.removedShopCartById(shopcart.getShopcartid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  shopcart.getStatus()  && 1 == shopCartMapper.insert(shopcart)) {
                        shopcartDA.removedShopCartById(shopcart.getShopcartid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  shopcart.getStatus() && 1 == shopCartMapper.updateByPrimaryKey(shopcart)) {
                        shopcartDA.removedShopCartById(shopcart.getShopcartid());
                    }
                    redisu.hdel(classname, shopcart.getShopcartid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (shopcartDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, ShopCartJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<ShopCart> re = new ArrayList<ShopCart>();          
            ShopCartExample shopcartExample = new ShopCartExample();
            shopcartExample.or().andShopcartidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            shopcartExample.or().andShopcartidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = shopCartMapper.selectByExample(shopcartExample);
            for (ShopCart value : re) {
                redisu.hset(classname, value.getShopcartid().toString(), value);
                shopcartDA.saveShopCart(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    ShopCart r = shopcartDA.findShopCartById(i);
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
        ShopcartUserDA shopcartUserDA=new ShopcartUserDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<ShopcartUser> re = new ArrayList<ShopcartUser>();          
            ShopcartUserExample shopcartUserExample = new ShopcartUserExample();
            shopcartUserExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)));
            shopcartUserExample.or().andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = shopcartUserMapper.selectByExample(shopcartUserExample);
            for (ShopcartUser value : re) {
                shopcartUserDA.saveShopcartUser(value);

                List<Integer> shopcartIdList = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(value.getShopcartList());
                shopcartIdList = JSONArray.toList(jsonArray, Integer.class);

                for(Integer shopcartId: shopcartIdList){
                    redisu.sAdd("shopcart_u"+value.getUserid().toString(), (Object)shopcartId);
                }

                if(andAll && userID == value.getUserid() && value.getShopcartSize() != 0){  
                    for(Integer shopcartId: shopcartIdList){
                        RefreshDBD(cacheService.PageID(shopcartId), refresRedis);
                    }
                }
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    ShopcartUser r = shopcartUserDA.findShopcartUserById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.sAdd("shopcart_u"+r.getUserid().toString(), (Object)r.getShopcartList()); 
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
