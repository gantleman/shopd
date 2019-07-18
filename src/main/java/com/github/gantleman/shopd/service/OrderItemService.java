package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    //only read
    public OrderItem getOrderItemByKey(Integer orderitemid, String url);

    public List<OrderItem> getOrderItemByOrderId(Integer orderid, String url);    

    //have write
    void insertOrderItem(OrderItem orderItem);

    public void TickBack();

    public void TickBack_extra();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);
}
