package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.ShopCartDA;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.service.jobs.ShopCartJob;
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

@Service("addShopCart")
public class ShopCartServiceImpl implements ShopCartService {

    @Autowired(required = false)
    ShopCartMapper shopCartMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private ShopCartJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "ShopCart";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, ShopCartJob.class, null, job);
        }
    }

    @Override
    public List<ShopCart> selectByID(Integer UserID) {
        List<ShopCart> re = null;

        if(redisu.hasKey("ShopCart_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("ShopCart_u"+UserID.toString());
            re = new ArrayList<ShopCart>();
            for (Object id : ro) {
                ShopCart r =  (ShopCart) redisu.hget(classname, ((Integer)id).toString());
                if (r != null)
                    re.add(r);
            }
            redisu.expire("ShopCart_u"+UserID.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            ShopCartExample shopcartExample=new ShopCartExample();
            shopcartExample.or().andUseridEqualTo(UserID);
            
            re = shopCartMapper.selectByExample(shopcartExample);

            ///read and write
            if(!redisu.hasKey("ShopCart_u"+UserID.toString())) {
                for( ShopCart item : re ){
                    redisu.sAddAndTime("ShopCart_u"+item.getUserid().toString(), 0, (Object)item.getShopcartid());
                    redisu.hset(classname, item.getShopcartid().toString(), item);
                }
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public void addShopCart(ShopCart shopcart) {
        RefreshDBD(shopcart.getUserid());

        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        shopcart.setShopcartid(new Long(id).intValue());
        shopcart.MakeStamp();
        shopcart.setStatus(2);
        shopcartDA.saveShopCart(shopcart);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, shopcart.getShopcartid().toString(), shopcart, 0);
    }

    @Override
    public void deleteByKey(Integer userid, Integer goodsid) {
        RefreshDBD(userid);
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        List <ShopCart> shopCart = shopcartDA.findAllShopCartByUGID(userid, goodsid);

        for(ShopCart item : shopCart){
            item.MakeStamp();
            item.setStatus(1);
            shopcartDA.saveShopCart(item);
            redisu.hdel(classname, item.getShopcartid().toString());        
        }
    }

    @Override
    public void updateCartByKey(ShopCart ishopcart) {
        RefreshDBD(ishopcart.getUserid());

        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        ShopCart shopcart = shopcartDA.findShopCartById(ishopcart.getShopcartid());
 
        if (shopcart != null)
        {
            ishopcart.MakeStamp();
            ishopcart.setStatus(3);
            
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
        RefreshDBD(userid);
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        List <ShopCart> shopCart = shopcartDA.findAllShopCartByUGID(userid, goodsid);

        if( shopCart.isEmpty())
        return null;
        else 
        return shopCart.get(0);
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());
        List<ShopCart> lshopcart = shopcartDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (ShopCart shopcart : lshopcart) {
            if(null ==  shopcart.getStatus()) {
                shopcartDA.removedShopCartById(shopcart.getShopcartid());
            }

            if(1 ==  shopcart.getStatus() && 1 == shopCartMapper.deleteByPrimaryKey(shopcart.getShopcartid  ())) {
                shopcartDA.removedShopCartById(shopcart.getShopcartid());
            }

            if(2 ==  shopcart.getStatus()  && 1 == shopCartMapper.insert(shopcart)) {
                shopcartDA.removedShopCartById(shopcart.getShopcartid());
            }

            if(3 ==  shopcart.getStatus() && 1 == shopCartMapper.updateByPrimaryKey(shopcart)) {
                shopcartDA.removedShopCartById(shopcart.getShopcartid());
            }
        }

        if (shopcartDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD(Integer userid) {
        ///init
       if (cacheService.IsCache(classname, userid)) {
           BDBEnvironmentManager.getInstance();
           ShopCartDA shopcartDA=new ShopCartDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<ShopCart> re = new ArrayList<ShopCart>();

           ShopCartExample shopcartExample = new ShopCartExample();
           shopcartExample.or().andUseridEqualTo(userid);
           re = shopCartMapper.selectByExample(shopcartExample);
           for (ShopCart value : re) {
               value.MakeStamp();
               shopcartDA.saveShopCart(value);

               redisu.sAddAndTime("ShopCart_u"+userid.toString(), 0, value.getShopcartid()); 
               redisu.hset(classname, value.getShopcartid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, ShopCartJob.class, null, job);          
           }
       }
   }
}
