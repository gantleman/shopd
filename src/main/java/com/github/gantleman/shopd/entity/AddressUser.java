package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.PrimaryKey;

public class AddressUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer addressSize;

    private String addressList;

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

    public Integer getAddressSize() {
        return addressSize;
    }

    public void setAddressSize(Integer addressSize) {
        this.addressSize = addressSize;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList == null ? null : addressList.trim();
    }
}