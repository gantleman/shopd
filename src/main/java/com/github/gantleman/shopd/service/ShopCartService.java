package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ShopCart;
import com.github.gantleman.shopd.entity.ShopCartKey;
import java.util.List;

public interface ShopCartService {
    //only read
    public List<ShopCart> selectByID(Integer UserID);
    
    //have write
    public void addShopCart(ShopCart shopCart);

    public void deleteByKey(ShopCartKey shopCartKey);

    public void updateCartByKey(ShopCart shopCart);

    public ShopCart selectCartByKey(ShopCartKey shopCartKey);
}
