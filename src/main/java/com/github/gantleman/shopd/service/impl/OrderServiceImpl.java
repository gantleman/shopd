package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.OrderMapper;
import com.github.gantleman.shopd.dao.OrderItemMapper;
import com.github.gantleman.shopd.dao.AddressMapper;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderExample;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Override
    public void insertOrder(Order order) {
        orderMapper.insertSelective(order);
    }

    @Override
    public void deleteById(Integer orderid) {
        orderMapper.deleteByPrimaryKey(orderid);
    }


    @Override
    public List<Order> selectOrderByIssen() {
        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(false);

        return orderMapper.selectByExample(orderExample);
    }

    @Override
    public List<Order> selectOrderByIssendAndIsreceive() {
        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(true).andIsreceiveEqualTo(false);

        return orderMapper.selectByExample(orderExample);
    }
    
    @Override
    public List<Order> selectOrderByIssendAndIsreceiveAndIscomplete() {
        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(true).andIsreceiveEqualTo(true).andIscompleteEqualTo(true);
 
        return orderMapper.selectByExample(orderExample);
    }

    public List<Order> selectOrderByIUserID(Integer ID) {

        OrderExample orderExample=new OrderExample();
        orderExample.or().andUseridEqualTo(ID);

        return orderMapper.selectByExample(orderExample);
    }

    @Override
    public void updateOrderByKey(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public Order selectByPrimaryKey(Integer orderid) {
        return orderMapper.selectByPrimaryKey(orderid);
    }
}
