package com.github.gantleman.shopd.entity;

import java.util.Date;

import com.github.gantleman.shopd.util.TimeUtils;
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

    private Integer status;

    private long stamp;

    public void MakeStamp() {
        setStamp(TimeUtils.getTimeWhitLong());
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

    /**
     * @return the stamp
     */
    public long getStamp() {
        return stamp;
    }

    /**
     * @param stamp the stamp to set
     */
    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

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