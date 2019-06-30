package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.OrderMapper;
import com.github.gantleman.shopd.dao.OrderItemMapper;
import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderExample;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderitemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> getOrderItemByExample(OrderItemExample orderItemExample) {
        return orderItemMapper.selectByExample(orderItemExample);
    }

    public List<OrderItem> getOrderItemByID(Integer id) {

        OrderItemExample orderItemExample=new OrderItemExample();
        orderItemExample.or().andOrderidEqualTo(id);

        return orderItemMapper.selectByExample(orderItemExample);
    }

    @Override
    public void insertOrderItem(OrderItem orderItem) {
        orderItemMapper.insertSelective(orderItem);
    }
}
