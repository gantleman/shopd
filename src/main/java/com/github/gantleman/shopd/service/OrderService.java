package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderExample;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.entity.Address;
import java.util.List;

public interface OrderService {

    //only read
    public List<Order> selectOrderByExample(OrderExample orderExample);
    
    public List<Order> selectOrderByIUserID(Integer ID);  

    public List<OrderItem> getOrderItemByExample(OrderItemExample orderItemExample);    

    public List<OrderItem> getOrderItemByID(Integer id);
    
    public Address getAddressByKey(Integer addressid);

    //have write
    public void insertOrder(Order order);

    public void deleteById(Integer orderid);

    public void updateOrderByKey(Order order);

    public Order selectByPrimaryKey(Integer orderid);

    void insertOrderItem(OrderItem orderItem);
}
