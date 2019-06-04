package com.github.gantleman.shopd.entity;

import java.util.Date;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class ShopCart extends ShopCartKey {
    private Date catedate;

    private Integer goodsnum;

    @PrimaryKey(sequence = "ID")
    private Integer userid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer goodsid;

    public Date getCatedate() {
        return catedate;
    }

    public void setCatedate(Date catedate) {
        this.catedate = catedate;
    }

    public Integer getGoodsnum() {
        return goodsnum;
    }

    public void setGoodsnum(Integer goodsnum) {
        this.goodsnum = goodsnum;
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