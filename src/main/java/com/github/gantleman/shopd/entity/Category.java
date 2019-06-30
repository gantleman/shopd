package com.github.gantleman.shopd.entity;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Category {

    @PrimaryKey(sequence = "ID")
    private Integer cateid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String catename;

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

    public Integer getCateid() {
        return cateid;
    }

    public void setCateid(Integer cateid) {
        this.cateid = cateid;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename == null ? null : catename.trim();
    }
}