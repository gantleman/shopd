package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    //only read
    public OrderItem getOrderItemByKey(Integer orderitemid, String url);

    public List<OrderItem> getOrderItemByOrderId(Integer orderid, String url);    

    //have write
    void insertOrderItem(OrderItem orderItem);

    public void Clean(Boolean all) ;

    public void Clean_extra(Boolean all);

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);
}
