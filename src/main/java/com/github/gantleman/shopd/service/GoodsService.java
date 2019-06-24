package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteKey;
import java.util.List;

public interface GoodsService {

    //only read
    public List<Goods> selectByAll();
    
    public List<Goods> selectByID(List<Integer> id);
    
    public List<Goods> selectByName(String name);
    
    public List<Goods> selectByDetailcateAndID(String cat, List<Integer> cateId);

    public Goods selectById(Integer goodsid);

    public List<Goods> selectByExampleLimit(List<Integer> digCateId);

    public Favorite selectFavByKey(FavoriteKey favoriteKey);

    public List<Favorite> selectFavByExample(Integer userid);

    //have write
    public Integer addGoods(Goods goods);

    public void addImagePath(ImagePath imagePath);

    public void deleteGoodsById(Integer goodsid);

    public void updateGoodsById(Goods goods);

    public List<ImagePath> findImagePath(Integer goodsid);

    public void addFavorite(Favorite favorite);

    public void deleteFavByKey(FavoriteKey favoriteKey);
}
