package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.GoodsMapper;
import com.github.gantleman.shopd.dao.FavoriteMapper;
import com.github.gantleman.shopd.dao.ImagePathMapper;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.GoodsExample;
import com.github.gantleman.shopd.entity.ImagePathExample;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import com.github.gantleman.shopd.entity.FavoriteKey;
import com.github.gantleman.shopd.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 文辉 on 2017/7/19.
 */

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsMapper goodsMapper;

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Autowired(required = false)
    FavoriteMapper favoriteMapper;

    @Override
    public Integer addGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
        return goods.getGoodsid();
    }

    @Override
    public void addImagePath(ImagePath imagePath) {
        imagePathMapper.insertSelective(imagePath);
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
    public List<ImagePath> findImagePath(Integer goodsid) {
        ImagePathExample imagePathExample = new ImagePathExample();
        imagePathExample.or().andGoodidEqualTo(goodsid);

        return imagePathMapper.selectByExample(imagePathExample);
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

    @Override
    public void addFavorite(Favorite favorite) {
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
}
