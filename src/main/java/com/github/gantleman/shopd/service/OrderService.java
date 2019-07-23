package com.github.gantleman.shopd.service;

import java.util.List;

import com.github.gantleman.shopd.entity.Order;

public interface OrderService {

    //only read
    public List<Order> selectOrderByAll(Integer pageId,  String url);

    public List<Order> selectOrderByIssendAndIsreceive(String url);

    public List<Order> selectOrderByIssend(String url);
    
    public List<Order> selectOrderByIUserID(Integer UserID, String url);

    public Order getOrderByKey(Integer orderid, String url);

    //have write
    public void insertOrder(Order order);

    public void deleteById(Integer orderid);

    public void updateOrderByKey(Order order);

    public void Clean(Boolean all) ;

    public void Clean_extra(Boolean all);

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);

    public void RefreshIsDBD(boolean refresRedis);
}
