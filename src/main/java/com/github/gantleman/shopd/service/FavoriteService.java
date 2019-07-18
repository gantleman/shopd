package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Favorite;
import java.util.List;

public interface FavoriteService {

    //only read
    public Favorite getFavoriteByKey(Integer favoriteid, String url);

    public Favorite selectFavByKey(Integer userid, Integer goodsid, String url);

    public List<Favorite> selectFavByUser(Integer userid, String url);

    //have write
    public void insertFavorite(Favorite favorite);

    public void deleteFavByKey(Integer userid, Integer goodsid);

    public void TickBack_extra();

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis);
}
