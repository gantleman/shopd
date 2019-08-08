package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;

@Entity
public class OrderitemOrder implements Serializable {
    private Integer orderid;

    private ArrayList<Integer> orderitemList;

    private static final long serialVersionUID = 1L;

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Integer getOrderitemSize() {
        return orderitemList.size();
    }

    public List<Integer>  getOrderitemList() {
        return orderitemList;
    }

    public void removeOrderitemList(Integer orderitemID) {
        this.orderitemList.remove(orderitemID);
    }

    public void addOrderitemList(Integer orderitemID) {
        this.orderitemList.add(orderitemID);
    } 
}