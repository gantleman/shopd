package com.github.gantleman.shopd.entity;

import java.util.Date;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Comment {

    @PrimaryKey(sequence = "ID")
    private Integer commentid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer userid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer goodsid;

    private Integer point;

    private String content;

    private Date commenttime;

    private String username;

    long stamp;

    public void MakeStamp() {
        stamp = TimeUtils.getTimeWhitLong();
    }

    public Integer getCommentid() {
        return commentid;
    }

    public void setCommentid(Integer commentid) {
        this.commentid = commentid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(Date commenttime) {
        this.commenttime = commenttime;
    }

    public void setUserName(String userName) {this.username=userName;}

    public String getUsername() {return username;}
}