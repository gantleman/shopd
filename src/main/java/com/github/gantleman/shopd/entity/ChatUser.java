package com.github.gantleman.shopd.entity;

import java.io.Serializable;

import com.sleepycat.persist.model.PrimaryKey;

public class ChatUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private Integer chatSize;

    private String chatList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getChatSize() {
        return chatSize;
    }

    public void setChatSize(Integer chatSize) {
        this.chatSize = chatSize;
    }

    public String getChatList() {
        return chatList;
    }

    public void setChatList(String chatList) {
        this.chatList = chatList == null ? null : chatList.trim();
    }
}