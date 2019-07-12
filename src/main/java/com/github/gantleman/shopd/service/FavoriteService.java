package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Favorite;
import java.util.List;

public interface FavoriteService {

    //only read
    public Favorite selectFavByKey(Integer userid, Integer goodsid);

    public List<Favorite> selectFavByUser(Integer userid);

    //have write
    public void insertFavorite(Favorite favorite);

    public void deleteFavByKey(Integer userid, Integer goodsid);

    public void TickBack();

    public void RefreshDBD(Integer userid);
}
