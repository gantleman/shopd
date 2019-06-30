package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import java.util.List;

public interface GoodsService {

    //only read
    public List<Goods> selectByAll();
    
    public List<Goods> selectByID(List<Integer> id);
    
    public List<Goods> selectByName(String name);
    
    public List<Goods> selectByDetailcateAndID(String cat, List<Integer> cateId);

    public Goods selectById(Integer goodsid);

    public List<Goods> selectByExampleLimit(List<Integer> digCateId);

    //have write
    public Integer insertGoods(Goods goods);

    public void deleteGoodsById(Integer goodsid);

    public void updateGoodsById(Goods goods);
}
