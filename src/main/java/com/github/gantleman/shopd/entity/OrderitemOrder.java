package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;

@Entity
public class OrderitemOrder implements Serializable {
    private Integer orderid;

    private Integer orderitemSize;

    private String orderitemList;

    private static final long serialVersionUID = 1L;

    private Integer status;

    public Integer getOrderid() {
        return orderid;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
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