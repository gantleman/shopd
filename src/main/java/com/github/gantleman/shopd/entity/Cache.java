package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Cache implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer cId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String cName;

    private Long cIndex;

    private String cHost;

    private String cHost2;

    private Long cStamp;

    private Long cStamp2;

    private static final long serialVersionUID = 1L;

    private Map<Integer, Integer> userid;

    /**
     * @return the userid
     */
    public Map<Integer, Integer> getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(Map<Integer, Integer> userid) {
        this.userid = userid;
    }
    
    public Integer getcId() {
        return cId;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName == null ? null : cName.trim();
    }

    public Long getcIndex() {
        return cIndex;
    }

    public void setcIndex(Long cIndex) {
        this.cIndex = cIndex;
    }

    public String getcHost() {
        return cHost;
    }

    public void setcHost(String cHost) {
        this.cHost = cHost == null ? null : cHost.trim();
    }

    public String getcHost2() {
        return cHost2;
    }

    public void setcHost2(String cHost2) {
        this.cHost2 = cHost2 == null ? null : cHost2.trim();
    }

    public Long getcStamp() {
        return cStamp;
    }

    public void setcStamp(Long cStamp) {
        this.cStamp = cStamp;
    }

    public Long getcStamp2() {
        return cStamp2;
    }

    public void setcStamp2(Long cStamp2) {
        this.cStamp2 = cStamp2;
    }
}