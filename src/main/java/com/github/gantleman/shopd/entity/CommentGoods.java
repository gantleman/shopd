package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class CommentGoods implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer goodsid;

    private ArrayList<Integer> commentList;

    private static final long serialVersionUID = 1L;

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public Integer getCommentSize() {
        return commentList.size();
    }

    public ArrayList<Integer> getCommentList() {
        return commentList;
    }

    public void removeCommentList(Integer commentID) {
        this.commentList.remove(commentID);
    }

    public void addCommentList(Integer commentID) {
        this.commentList.add(commentID);
    }  
}