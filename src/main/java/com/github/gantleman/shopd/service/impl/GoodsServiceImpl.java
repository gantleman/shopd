package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.GoodsMapper;
import com.github.gantleman.shopd.dao.ImagePathMapper;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.GoodsExample;
import com.github.gantleman.shopd.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsMapper goodsMapper;

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Override
    public Integer insertGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
        return goods.getGoodsid();
    }

    @Override
    public List<Goods> selectByAll() {
        return goodsMapper.selectByExampleWithBLOBs(new GoodsExample());
    }

    @Override
    public List<Goods> selectByID(List<Integer> id) {

        GoodsExample goodsExample=new GoodsExample();
        goodsExample.or().andGoodsidIn(id);

        return goodsMapper.selectByExampleWithBLOBs(goodsExample);
    }

    @Override
    public List<Goods> selectByName(String name) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.or().andGoodsnameLike("%" + name + "%");

        return goodsMapper.selectByExampleWithBLOBs(goodsExample);
    }

    @Override
    public List<Goods> selectByDetailcateAndID(String cat, List<Integer> cateId ) {

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.or().andDetailcateLike("%" + cat + "%");
        if (!cateId.isEmpty()) {
            goodsExample.or().andCategoryIn(cateId);
        }

        return goodsMapper.selectByExampleWithBLOBs(goodsExample);
    }

    @Override
    public void deleteGoodsById(Integer goodsid) {

        goodsMapper.deleteByPrimaryKey(goodsid);
    }

    @Override
    public void updateGoodsById(Goods goods) {
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public Goods selectById(Integer goodsid) {
        return goodsMapper.selectByPrimaryKey(goodsid);
    }

    @Override
    public List<Goods> selectByExampleLimit(List<Integer> digCateId) {

        GoodsExample digGoodsExample = new GoodsExample();
        digGoodsExample.or().andCategoryIn(digCateId);
        return goodsMapper.selectByExampleWithBLOBsLimit(digGoodsExample);
    }
}
