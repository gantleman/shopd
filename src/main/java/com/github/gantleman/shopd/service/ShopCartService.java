package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ShopCart;
import java.util.List;

public interface ShopCartService {
    //only read
    public List<ShopCart> selectByID(Integer UserID);
    
    //have write
    public void addShopCart(ShopCart shopCart);

    public void updateCartByKey(ShopCart shopCart);

    public void deleteByKey(Integer userid, Integer goodsid);

    public ShopCart selectCartByKey(Integer userid, Integer goodsid);

    public void TickBack();

    public void RefreshDBD(Integer userid);
}
