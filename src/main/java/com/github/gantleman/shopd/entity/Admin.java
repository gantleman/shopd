package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Admin implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer adminid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String adminname;

    private String password;

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

    public Admin(Integer adminid, String adminname, String password) {
        this.adminid = adminid;
        this.adminname = adminname;
        this.password = password;
    }

    public Admin() {
    }

    public Integer getAdminid() {
        return adminid;
    }

    public void setAdminid(Integer adminid) {
        this.adminid = adminid;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname == null ? null : adminname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}