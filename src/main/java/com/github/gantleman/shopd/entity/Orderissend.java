package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Orderissend implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer orderid;

    private static final long serialVersionUID = 1L;

    /**
     * @return the status
     */
    public Integer getOrderid() {
        return orderid;
    }

    /**
     * @param status the status to set
     */
    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }
}