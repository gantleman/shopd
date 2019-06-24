package com.github.gantleman.shopd.entity;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Category {

    @PrimaryKey(sequence = "ID")
    private Integer cateid;

    private String catename;

    long stamp;

    public void MakeStamp() {
        stamp = TimeUtils.getTimeWhitLong();
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