package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteKey;
import java.util.List;

public interface FavoriteService {

    //only read
    public Favorite selectFavByKey(FavoriteKey favoriteKey);

    public List<Favorite> selectFavByExample(Integer userid);

    //have write
    public void insertFavorite(Favorite favorite);

    public void deleteFavByKey(FavoriteKey favoriteKey);

    public void TickBack();

    public void RefreshDBD(Integer userid);
}
