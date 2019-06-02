package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.*;
import java.util.List;

/**
 * Created by 文辉 on 2017/7/24.
 */
public interface ShopCartService {
    public void addShopCart(ShopCart shopCart);

    public List<ShopCart> selectByExample(ShopCartExample shopCartExample);

    public void deleteByKey(ShopCartKey shopCartKey);

    public void updateCartByKey(ShopCart shopCart);

    public ShopCart selectCartByKey(ShopCartKey shopCartKey);
}
