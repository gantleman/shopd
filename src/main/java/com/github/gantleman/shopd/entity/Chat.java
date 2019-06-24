package com.github.gantleman.shopd.entity;

import java.util.Date;

import com.github.gantleman.shopd.util.TimeUtils;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Chat {

    @PrimaryKey(sequence = "ID")
    private Integer chatid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer senduser;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private Integer receiveuser;

    private String msgcontent;

    private Date msgtime;

    long stamp;

    public void MakeStamp() {
        stamp = TimeUtils.getTimeWhitLong();
    }

    public Integer getChatid() {
        return chatid;
    }

    public void setChatid(Integer chatid) {
        this.chatid = chatid;
    }

    public Integer getSenduser() {
        return senduser;
    }

    public void setSenduser(Integer senduser) {
        this.senduser = senduser;
    }

    public Integer getReceiveuser() {
        return receiveuser;
    }

    public void setReceiveuser(Integer receiveuser) {
        this.receiveuser = receiveuser;
    }

    public String getMsgcontent() {
        return msgcontent;
    }

    public void setMsgcontent(String msgcontent) {
        this.msgcontent = msgcontent == null ? null : msgcontent.trim();
    }

    public Date getMsgtime() {
        return msgtime;
    }

    public void setMsgtime(Date msgtime) {
        this.msgtime = msgtime;
    }
}