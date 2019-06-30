package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Order;
import java.util.List;

public interface OrderService {

    //only read
    public List<Order> selectOrderByIssendAndIsreceiveAndIscomplete();

    public List<Order> selectOrderByIssendAndIsreceive();

    public List<Order> selectOrderByIssen();
    
    public List<Order> selectOrderByIUserID(Integer ID);  

    //have write
    public void insertOrder(Order order);

    public void deleteById(Integer orderid);

    public void updateOrderByKey(Order order);

    public Order selectByPrimaryKey(Integer orderid);
}
