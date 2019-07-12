package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    //only read
    public List<OrderItem> getOrderItemByOrderId(Integer orderid);    

    //have write
    void insertOrderItem(OrderItem orderItem);

    public void TickBack();

    public void RefreshDBD();
}
