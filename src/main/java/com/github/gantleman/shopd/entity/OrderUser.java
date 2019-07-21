package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class OrderUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer orderSize;

    private String orderList;

    private static final long serialVersionUID = 1L;

    private Integer status;

    public Integer getUserid() {
        return userid;
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

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(Integer orderSize) {
        this.orderSize = orderSize;
    }

    public String getOrderList() {
        return orderList;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList == null ? null : orderList.trim();
    }
}