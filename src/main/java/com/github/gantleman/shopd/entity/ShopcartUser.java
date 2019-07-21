package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ShopcartUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer shopcartSize;

    private String shopcartList;

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