package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class OrderUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private ArrayList<Integer> orderList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getOrderSize() {
        return orderList.size();
    }

    public List<Integer> getOrderList() {
        return orderList;
    }

    public void removeOrderList(Integer orderid) {
        this.orderList.remove(orderid);
    }

    public void addOrderList(Integer orderid) {
        this.orderList.add(orderid);
    } 
}