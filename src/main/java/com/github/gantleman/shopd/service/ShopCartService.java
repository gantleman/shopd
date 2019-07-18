package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ShopCart;
import java.util.List;

public interface ShopCartService {
    //only read
    public List<ShopCart> selectByID(Integer UserID, String url);

    public ShopCart getShopCartByKey(Integer addressid, String url);
    
    //have write
    public ShopCart selectCartByKey(Integer userid, Integer goodsid);
    
    public void addShopCart(ShopCart shopCart);

    public void updateCartByKey(ShopCart shopCart);

    public void deleteByPrimaryKey(Integer shopcartid);

    public void deleteByKey(Integer userid, Integer goodsid);

    public void TickBack();

    public void TickBack_extra();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);
}
