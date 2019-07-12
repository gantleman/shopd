package com.github.gantleman.shopd.entity;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class ImagePath {

    @PrimaryKey(sequence = "ID")
    private Integer pathid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer goodid;

    private String path;

    private Integer status;

    private long stamp;

    public void MakeStamp() {
        setStamp(TimeUtils.getTimeWhitLong());
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

    public ImagePath() {
    }

    public ImagePath(Integer pathid, Integer goodid, String path) {

        this.pathid = pathid;
        this.goodid = goodid;
        this.path = path;
    }

    public Integer getPathid() {
        return pathid;
    }

    public void setPathid(Integer pathid) {
        this.pathid = pathid;
    }

    public Integer getGoodid() {
        return goodid;
    }

    public void setGoodid(Integer goodid) {
        this.goodid = goodid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }
}