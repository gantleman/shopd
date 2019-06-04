package com.github.gantleman.shopd.entity;

import java.util.Date;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Favorite extends FavoriteKey {

    @PrimaryKey(sequence = "ID")
    private Integer userid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer goodsid;

    private Date collecttime;

    public Date getCollecttime() {
        return collecttime;
    }

    public void setCollecttime(Date collecttime) {
        this.collecttime = collecttime;
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
}