package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.PrimaryKey;

public class FavoriteUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer favoriteSize;

    private String favoriteList;

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

    public Integer getFavoriteSize() {
        return favoriteSize;
    }

    public void setFavoriteSize(Integer favoriteSize) {
        this.favoriteSize = favoriteSize;
    }

    public String getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(String favoriteList) {
        this.favoriteList = favoriteList == null ? null : favoriteList.trim();
    }
}