package com.github.gantleman.shopd.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ChatUser implements Serializable {
    @PrimaryKey(sequence = "ID")
    private Integer userid;

    private ArrayList<Integer> chatList;

    private static final long serialVersionUID = 1L;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public List<Integer> getChatList() {
        return chatList;
    }

    public Integer getChatSize() {
        return chatList.size();
    }

    public void removeChatList(Integer chatID) {
        this.chatList.remove(chatID);
    }

    public void addChatList(Integer chatID) {
        this.chatList.add(chatID);
    }  
}