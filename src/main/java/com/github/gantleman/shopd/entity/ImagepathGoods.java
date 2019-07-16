package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.PrimaryKey;

public class ImagepathGoods implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer goodsid;

    private Integer imagepathSize;

    private String imagepathList;

    private static final long serialVersionUID = 1L;

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public Integer getImagepathSize() {
        return imagepathSize;
    }

    public void setImagepathSize(Integer imagepathSize) {
        this.imagepathSize = imagepathSize;
    }

    public String getImagepathList() {
        return imagepathList;
    }

    public void setImagepathList(String imagepathList) {
        this.imagepathList = imagepathList == null ? null : imagepathList.trim();
    }
}