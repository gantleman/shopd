package com.github.gantleman.shopd.service;

import java.util.List;

import com.github.gantleman.shopd.entity.Goods;

public interface GoodsService {

    //only read
    public List<Goods> selectByAll(Integer pageid, String url);
    
    public List<Goods> selectByID(List<Integer> id, String url);
    
    public List<Goods> selectByName(String name, String url);
    
    public List<Goods> selectByDetailcateAndID(List<Integer> cateId, String url);

    public Goods selectById(Integer goodsid, String url);

    public List<Goods> selectByCateLimit(List<Integer> digCateId, String url);

    //have write
    public Integer insertGoods(Goods goods);

    public void deleteGoodsById(Integer goodsid);

    public void updateGoodsById(Goods goods);

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshDBD(String name, boolean refresRedis);
}
