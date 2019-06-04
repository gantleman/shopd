package com.github.gantleman.shopd.entity;
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