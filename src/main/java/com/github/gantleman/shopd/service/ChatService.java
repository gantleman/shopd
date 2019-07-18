package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Chat;

import java.util.List;

public interface ChatService {

    //only read
    public Chat getChatByKey(Integer chatid, String url);

    public List<Chat> selectChatBySend(Integer UserID, String url);
    
    public List<Chat> selectChatByReceive(Integer Receive, String url);
    
    public List<Chat> selectChatBySendAndReceive(Integer Send, Integer Receive, String url);

    //have write
    public void insertChatSelective(Chat chat);

    public void TickBack_extra();

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis);
}
