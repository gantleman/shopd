package com.github.gantleman.shopd.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Address {

    @PrimaryKey(sequence = "ID")
    private Integer addressid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer userid;

    private String province;

    private String city;

    private String county;

    private String detailaddr;

    private String conname;

    private String contel;

    public Integer getAddressid() {
        return addressid;
    }

    public void setAddressid(Integer addressid) {
        this.addressid = addressid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county == null ? null : county.trim();
    }

    public String getDetailaddr() {
        return detailaddr;
    }

    public void setDetailaddr(String detailaddr) {
        this.detailaddr = detailaddr == null ? null : detailaddr.trim();
    }

    public String getConname() {
        return conname;
    }

    public void setConname(String conname) {
        this.conname = conname == null ? null : conname.trim();
    }

    public String getContel() {
        return contel;
    }

    public void setContel(String contel) {
        this.contel = contel == null ? null : contel.trim();
    }
}