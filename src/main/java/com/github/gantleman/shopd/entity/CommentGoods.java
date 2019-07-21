package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class CommentGoods implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer goodsid;

    private Integer commentSize;

    private String commentList;

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

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public String getCommentList() {
        return commentList;
    }

    public void setCommentList(String commentList) {
        this.commentList = commentList == null ? null : commentList.trim();
    }
}