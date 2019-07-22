package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsMapper goodsMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private GoodsJob job;
    
    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Goods";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, GoodsJob.class, null, job);
        }
    }

    @Override
    public List<Goods> selectByAll(Integer pageId, String url) {
        List<Goods> re = new ArrayList<Goods>();

        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Goods r = (Goods) redisu.hget(classname, pageId.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/goodspage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Goods r = (Goods) redisu.hget(classname, i.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }
        return re;
    }

    @Override
    public Goods selectById(Integer goodsid, String url) {
        Goods re = null;
        Integer pageId = cacheService.PageID(goodsid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Object o = redisu.hget(classname, goodsid.toString());
            if(o != null){
                re = (Goods) o;
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/goodspage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                Object o = redisu.hget(classname, goodsid.toString());
                if(o != null){
                    re = (Goods) o;
                    redisu.hincr(classname+"pageid", pageId.toString(), 1);
                }
            }   
        }
        return re;
    }

    @Override
    public List<Goods> selectByID(List<Integer> id, String url) {
        List<Goods> lg = new ArrayList<Goods>();
        for(Integer i : id){
            Goods r = selectById(i, url);
            if(r != null)
                lg.add(r);
        }
        return lg;
    }

    @Override
    public List<Goods> selectByName(String name, String url) {
        //Lucene should be used here, with a brief function
        List<Goods> re = new ArrayList<>();

        if(redisu.hHasKey("goods_n", name)) {
            //read redis
            Object id = redisu.hget("goods_n", name);
            Goods r =  selectById((Integer)id, url);
            if (r != null)
                re.add(r);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/goodspagename", name);
            }else{
                RefreshDBD(name, true);
            }
            if(redisu.hHasKey("goods_n", name)) {
                //read redis
                Object id = redisu.hget("goods_n", name);
                if(id != null){
                    Goods r =  selectById((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
            }
        }
        return re;
    }

    @Override
    public List<Goods> selectByDetailcateAndID(List<Integer> cateId, String url) {
        //Big data should be used here, and the functions are brief.
        List<Goods> re = new ArrayList<>();
        Map<Object,Object> rm = redisu.hmget(classname);
        for(Object rv : rm.values()){
            Goods g = (Goods) rv;
            for(Integer i : cateId){
                if(g.getCategory() == i)
                    re.add(g);
            }
        }
        return re;
    }

    @Override
    public List<Goods> selectByCateLimit(List<Integer> digCateId, String url) {
        //Big data should be used here, and the functions are brief.
        List<Goods> re = new ArrayList<Goods>();
        Map<Object,Object> rm = redisu.hmget(classname);
        for(Object rv : rm.values()){
            Goods g = (Goods) rv;
            for(Integer i : digCateId){
                if(g.getCategory() == i)
                    re.add(g);

                if(re.size() > 5)
                    break;
            }
        }
        return re;
    }

    @Override
    public Integer insertGoods(Goods goods) {
        RefreshDBD(cacheService.PageID(goods.getGoodsid()), false);

        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        goods.setGoodsid(new Long(id).intValue());
        goods.setStatus(CacheService.STATUS_INSERT);
        goodsDA.saveGoods(goods);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset("goods_n", goods.getGoodsname(), goods.getGoodsid());
        redisu.hset(classname, goods.getGoodsid().toString(), goods, 0);
        return goods.getGoodsid();
    }

    @Override
    public void deleteGoodsById(Integer goodsid) {
        RefreshDBD(cacheService.PageID(goodsid), false);

        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
        Goods goods = goodsDA.findGoodsById(goodsid);

        if(goods != null && goods.getStatus() == CacheService.STATUS_INSERT){
                goodsDA.removedGoodsById(goodsid);
                //Re-publish to redis
                redisu.hdel("goods_n", goods.getGoodsname());
                redisu.hdel(classname, goods.getGoodsid().toString());
        } else if (goods != null)
        {
                goods.setStatus(CacheService.STATUS_DELETE);
                goodsDA.saveGoods(goods);

                //Re-publish to redis
                redisu.hdel("goods_n", goods.getGoodsname());
                redisu.hdel(classname, goods.getGoodsid().toString());
        }
    }

    @Override
    public void updateGoodsById(Goods igoods) {

        RefreshDBD(cacheService.PageID(igoods.getGoodsid()), false);

        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
        Goods goods = goodsDA.findGoodsById(igoods.getGoodsid());
 
        if (goods != null && goods.getStatus() != CacheService.STATUS_DELETE)
        {
            if(igoods.getStatus()== null)
                igoods.setStatus(CacheService.STATUS_UPDATE);
            
            if(igoods.getActivity() != null){
                goods.setActivity(igoods.getActivity());
            }
            if(igoods.getGoodsid() != null){
                goods.setGoodsid(igoods.getGoodsid());
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
        List<Integer> listid = cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Goods goods = goodsDA.findGoodsById(i);
                if(goods != null){
                    if(null ==  goods.getStatus()) {
                        goodsDA.removedGoodsById(goods.getGoodsid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  goods.getStatus() && 1 == goodsMapper.deleteByPrimaryKey(goods.getGoodsid())) {
                        goodsDA.removedGoodsById(goods.getGoodsid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  goods.getStatus()  && 1 == goodsMapper.insert(goods)) {
                        goodsDA.removedGoodsById(goods.getGoodsid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  goods.getStatus() && 1 == goodsMapper.updateByPrimaryKey(goods)) {
                        goodsDA.removedGoodsById(goods.getGoodsid());
                    } 
                    redisu.hdel("goods_n", goods.getGoodsname());
                    redisu.hdel(classname, goods.getGoodsid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (goodsDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, GoodsJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Goods> re = new ArrayList<Goods>();          
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.or().andGoodsidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            goodsExample.or().andGoodsidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = goodsMapper.selectByExample(goodsExample);
            for (Goods value : re) {
                redisu.hset("goods_n", value.getGoodsname(), value.getGoodsid());
                redisu.hset(classname, value.getGoodsid().toString(), value);
                goodsDA.saveGoods(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Goods r = goodsDA.findGoodsById(i);
                    if(r != null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset("goods_n", r.getGoodsname(), r.getGoodsid());
                        redisu.hset(classname, i.toString(), r);                          
                    }           
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshDBD(String name, boolean refresRedis) {
        BDBEnvironmentManager.getInstance();
        GoodsDA goodsDA=new GoodsDA(BDBEnvironmentManager.getMyEntityStore());
        List<Goods> goods = goodsDA.findAllGoodsByGoodsname(name);
        if(goods.isEmpty()){
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.or().andGoodsnameEqualTo(name);
            goods = goodsMapper.selectByExample(goodsExample);
            
            for(Goods g : goods){
                RefreshDBD(cacheService.PageID(g.getGoodsid()), refresRedis);
            }     
        }
    }
}
