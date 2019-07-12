package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.GoodsDA;
import com.github.gantleman.shopd.dao.GoodsMapper;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.GoodsExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.jobs.GoodsJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsMapper goodsMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private GoodsJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Goods";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, GoodsJob.class, null, job);
        }
    }

    @Override
    public List<Goods> selectByAll() {

        List<Goods> re = new ArrayList<Goods>();
        if(redisu.hasKey(classname)) {
            //read redis
            Map<Object, Object> rm = redisu.hmget(classname);
            for (Object value : rm.values()) {
                re.add( (Goods)value);
            }

            redisu.expire(classname, 0);
        }else {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            re = goodsMapper.selectByExample(new GoodsExample());
            for (Goods value : re) {
                tmap.put(value.getGoodsid().toString(), (Object)value);
                redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Goods> selectByID(List<Integer> id) {

        List<Goods> re = new ArrayList<Goods>();
        if(redisu.hasKey(classname)) {
            //read redis
            for(Integer iid: id){
                Object value = redisu.hget(classname, iid.toString());
                if(value != null)
                    re.add( (Goods)value);
            }
            redisu.expire(classname, 0);
        }else {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            re = goodsMapper.selectByExample(new GoodsExample());
            for (Goods value : re) {
                tmap.put(value.getGoodsid().toString(), (Object)value);
                redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Goods> selectByName(String name) {
        if(!redisu.hasKey(classname)) {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            List<Goods> re = goodsMapper.selectByExample(new GoodsExample());
            for (Goods value : re) {
                tmap.put(value.getGoodsid().toString(), (Object)value);
                redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }

        List<Goods> re = new ArrayList<Goods>();
        Map<Object, Object> rm = redisu.hsacn("Goods_n", "*" + name + "*");
        for(Object value : rm.values()){
            Integer mapvalue = (Integer) value;
            Object gv = redisu.hget(classname, mapvalue.toString());
            re.add((Goods) gv); 
        }
        return re;
    }

    @Override
    public List<Goods> selectByDetailcateAndID(String cat, List<Integer> cateId ) {
        if(!redisu.hasKey(classname)) {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            List<Goods> re = goodsMapper.selectByExample(new GoodsExample());
            for (Goods value : re) {
                tmap.put(value.getGoodsid().toString(), (Object)value);
                redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }

        List<Goods> re = new ArrayList<Goods>();
        for(Integer cid : cateId){
            Set<Object> ro = redisu.sGet("Goods_c"+cid.toString());
            for(Object value: ro){
                Integer gid = (Integer) value; 
                Object gv = redisu.hget(classname, gid.toString());
                re.add((Goods) gv);
            }   
        }
        return re;        
    }

    @Override
    public Goods selectById(Integer goodsid) {
        Goods re = new Goods();
        if(redisu.hHasKey(classname, goodsid.toString())) {
            //read redis
            Object rm = redisu.hget(classname, goodsid.toString());

            re = (Goods) rm;
            redisu.expire(classname, 0);
        }else {
            if(redisu.hasKey(classname)){
                //write redis
                Map<String, Object> tmap = new HashMap<>();
                List<Goods> lre = new ArrayList<Goods>();
                lre = goodsMapper.selectByExample(new GoodsExample());
                for (Goods value : lre) {
                    tmap.put(value.getGoodsid().toString(), (Object)value);
                    redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                    redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
                    if(value.getGoodsid() == goodsid)
                        re = value;
                }

                ///read and write
                if(!redisu.hasKey(classname)) {
                    redisu.hmset(classname, tmap, 0);
                }                 
            }
        }
        return re;
    }

    @Override
    public List<Goods> selectByCateLimit(List<Integer> digCateId) {

        List<Goods> re = new ArrayList<Goods>();

        for(Integer cid: digCateId){
            if(redisu.hasKey("Goods_c"+cid.toString())) {
                //read redis
                Set<Object> rm = redisu.sGet("Goods_c"+cid.toString());

                for(Object i: rm){
                    Goods r = (Goods)redisu.hget(classname, ((Integer)i).toString());
                    if(r!= null)
                        re.add(r);
                }
                redisu.expire("Goods_c"+cid.toString(), 0);
                redisu.expire(classname, 0);
            }else {
                if(redisu.hasKey(classname)){
                    //write redis
                    Map<String, Object> tmap = new HashMap<>();
                    List<Goods> lre = new ArrayList<Goods>();
                    lre = goodsMapper.selectByExample(new GoodsExample());
                    for (Goods value : lre) {
                        tmap.put(value.getGoodsid().toString(), (Object)value);
                        redisu.hset("Goods_n", value.getGoodsname(), value.getGoodsid(), 0);
                        redisu.sAddAndTime("Goods_c"+value.getCategory().toString(), 0, value.getGoodsid());
                        for(Integer iid: digCateId){
                            if(value.getCategory() == iid) {
                                re.add(value);
                            }
                        }
                    }

                    ///read and write
                    if(!redisu.hasKey(classname)) {
                        redisu.hmset(classname, tmap, 0);
                    }                 
                }
            }            
        }

        return re;
    }

    @Override
    public Integer insertGoods(Goods goods) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        goods.setGoodsid(new Long(id).intValue());
        goods.MakeStamp();
        goods.setStatus(2);
        goodsDA.saveGoods(goods);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, goods.getGoodsid().toString(), goods, 0);

        return 1;
    }

    @Override
    public void deleteGoodsById(Integer goodsid) {

        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
        Goods goods = goodsDA.findGoodsById(goodsid);
 
        if (goods != null)
        {
             goods.MakeStamp();
             goods.setStatus(1);
             goodsDA.saveGoods(goods);
 
             //Re-publish to redis
             redisu.hdel(classname, goods.getGoodsid().toString());
        } 
    }

    @Override
    public void updateGoodsById(Goods igoods) {

        RefreshDBD();

        BDBEnvironmentManager.getInstance();
       GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
       Goods goods = goodsDA.findGoodsById(igoods.getGoodsid());
 
        if (goods != null)
        {
            igoods.MakeStamp();
            igoods.setStatus(3);
            
            if(igoods.getActivity() != null){
                goods.setActivity(igoods.getActivity());
            }
            if(igoods.getActivityid() != null){
                goods.setActivityid(igoods.getActivityid());
            }
            if(igoods.getCategory() != null){
                goods.setCategory(igoods.getCategory());
            }
            if(igoods.getDescription() != null){
                goods.setDescription(igoods.getDescription());
            }
            if(igoods.getDetailcate() != null){
                goods.setDetailcate(igoods.getDetailcate());
            }
            if(igoods.getGoodsname() != null){
                goods.setGoodsname(igoods.getGoodsname());
            }
            if(igoods.getImagePaths() != null){
                goods.setImagePaths(igoods.getImagePaths());
            }
            if(igoods.getNewPrice() != null){
                goods.setNewPrice(igoods.getNewPrice());
            }
            if(igoods.getNum() != null){
                goods.setNum(igoods.getNum());
            }
            if(igoods.getPrice() != null){
                goods.setPrice(igoods.getPrice());
            } 
            goodsDA.saveGoods(goods);

            //Re-publish to redis
            redisu.hset(classname, goods.getGoodsid().toString(), (Object)goods, 0);
        }
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
        List<Goods> lgoods = goodsDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Goods goods : lgoods) {
            if(null ==  goods.getStatus()) {
                goodsDA.removedGoodsById(goods.getGoodsid());
            }

            if(1 ==  goods.getStatus() && 1 == goodsMapper.deleteByPrimaryKey(goods.getGoodsid())) {
                goodsDA.removedGoodsById(goods.getGoodsid());
            }

            if(2 ==  goods.getStatus()  && 1 == goodsMapper.insert(goods)) {
                goodsDA.removedGoodsById(goods.getGoodsid());
            }

            if(3 ==  goods.getStatus() && 1 == goodsMapper.updateByPrimaryKey(goods)) {
                goodsDA.removedGoodsById(goods.getGoodsid());
            }
        }

        if (goodsDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        ///init
       if (cacheService.IsCache(classname)) {
           BDBEnvironmentManager.getInstance();
           GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<Goods> re = new ArrayList<Goods>();

           GoodsExample goodsExample = new GoodsExample();
           re = goodsMapper.selectByExample(goodsExample);
           for (Goods value : re) {
               value.MakeStamp();
               goodsDA.saveGoods(value);

               redisu.hset(classname, value.getGoodsid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, GoodsJob.class, null, job);          
           }
       }
   }
}
