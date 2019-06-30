package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import java.util.List;

public interface OrderItemService {

    //only read
    public List<OrderItem> getOrderItemByExample(OrderItemExample orderItemExample);    

    public List<OrderItem> getOrderItemByID(Integer id);

    //have write
    void insertOrderItem(OrderItem orderItem);
}
