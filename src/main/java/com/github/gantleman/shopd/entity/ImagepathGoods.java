package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ImagepathGoods implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer goodsid;

    private Integer imagepathSize;

    private String imagepathList;

    private static final long serialVersionUID = 1L;

    private Integer status;

    public Integer getGoodsid() {
        return goodsid;
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