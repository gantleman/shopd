package com.github.gantleman.shopd.entity;

import java.io.Serializable;

public class OrderitemOrder implements Serializable {
    private Integer orderid;

    private Integer orderitemSize;

    private String orderitemList;

    private static final long serialVersionUID = 1L;

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Integer getOrderitemSize() {
        return orderitemSize;
    }

    public void setOrderitemSize(Integer orderitemSize) {
        this.orderitemSize = orderitemSize;
    }

    public String getOrderitemList() {
        return orderitemList;
    }

    public void setOrderitemList(String orderitemList) {
        this.orderitemList = orderitemList == null ? null : orderitemList.trim();
    }
}