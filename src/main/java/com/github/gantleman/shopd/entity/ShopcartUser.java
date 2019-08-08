package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ShopcartUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private ArrayList<Integer> shopcartList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getShopcartSize() {
        return shopcartList.size();
    }

    public List<Integer> getShopcartList() {
        return shopcartList;
    }

    public void removeShopcartList(Integer Shopcartid) {
        this.shopcartList.remove(Shopcartid);
    }

    public void addShopcartList(Integer Shopcartid) {
        this.shopcartList.add(Shopcartid);
    } 

}