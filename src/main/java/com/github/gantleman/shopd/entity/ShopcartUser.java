package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.PrimaryKey;

public class ShopcartUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer shopcartSize;

    private String shopcartList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getShopcartSize() {
        return shopcartSize;
    }

    public void setShopcartSize(Integer shopcartSize) {
        this.shopcartSize = shopcartSize;
    }

    public String getShopcartList() {
        return shopcartList;
    }

    public void setShopcartList(String shopcartList) {
        this.shopcartList = shopcartList == null ? null : shopcartList.trim();
    }
}