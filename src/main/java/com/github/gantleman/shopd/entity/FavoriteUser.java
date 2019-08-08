package com.github.gantleman.shopd.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class FavoriteUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private ArrayList<Integer> favoriteList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getFavoriteSize() {
        return favoriteList.size();
    }


    public List<Integer> getFavoriteList() {
        return favoriteList;
    }

    public void removeFavoriteList(Integer favoriteID) {
        this.favoriteList.remove(favoriteID);
    }

    public void addFavoriteList(Integer favoriteID) {
        this.favoriteList.add(favoriteID);
    }  
}