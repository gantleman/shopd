package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.Date;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class User implements Serializable{

    @PrimaryKey(sequence = "ID")
    private Integer userid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String username;

    private String password;

    private Date regtime;

    private String email;

    private String telephone;

    private long stamp;

    public User() {
        super();
        this.stamp = TimeUtils.getTimeWhitLong();
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

    void MakeStamp() {
        stamp = TimeUtils.getTimeWhitLong();
    }

    public User(String userName, String password) {
		super();
		this.username = userName;
        this.password = password;
        this.stamp = TimeUtils.getTimeWhitLong();
	}


	public User(Integer userId, String userName, String password) {
		super();
		this.userid = userId;
		this.username = userName;
        this.password = password;
        this.stamp = TimeUtils.getTimeWhitLong();
	}

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }
}