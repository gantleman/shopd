package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Order;
import java.util.List;

public interface OrderService {

    //only read
    public List<Order> selectOrderByIssendAndIsreceiveAndIscomplete();

    public List<Order> selectOrderByIssendAndIsreceive();

    public List<Order> selectOrderByIssend();
    
    public List<Order> selectOrderByIUserID(Integer UserID);  

    //have write
    public void insertOrder(Order order);

    public void deleteById(Integer orderid);

    public void updateOrderByKey(Order order);

    public void TickBack();

    public void RefreshDBD();
}
