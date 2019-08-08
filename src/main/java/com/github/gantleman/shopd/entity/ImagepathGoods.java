package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ImagepathGoods implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer goodsid;

    private ArrayList<Integer> imagepathList;

    private static final long serialVersionUID = 1L;

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public Integer getImagepathSize() {
        return imagepathList.size();
    }

    public List<Integer> getImagepathList() {
        return imagepathList;
    }

    public void removeImagePathList(Integer imagepathID) {
        this.imagepathList.remove(imagepathID);
    }

    public void addImagePathList(Integer imagepathID) {
        this.imagepathList.add(imagepathID);
    }  
}