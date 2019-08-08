package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class AddressUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private ArrayList<Integer> addressList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getAddressSize() {
        return addressList.size();
    }

    public void removeAddressList(Integer addressID) {
        this.addressList.remove(addressID);
    }

    public void addAddressList(Integer addressID) {
        this.addressList.add(addressID);
    }

    public List<Integer> getAddressList() {
        return this.addressList;
    }
}