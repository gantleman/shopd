package com.github.gantleman.shopd.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Deliver {

    @PrimaryKey(sequence = "ID")
    private Integer deliverid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer orderid;

    private Integer sendid;

    public Integer getDeliverid() {
        return deliverid;
    }

    public void setDeliverid(Integer deliverid) {
        this.deliverid = deliverid;
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Integer getSendid() {
        return sendid;
    }

    public void setSendid(Integer sendid) {
        this.sendid = sendid;
    }
}