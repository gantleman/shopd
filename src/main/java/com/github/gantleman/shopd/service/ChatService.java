package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Chat;

import java.util.List;

public interface ChatService {

    //only read
    public List<Chat> selectChatBySend(Integer Send);
    
    public List<Chat> selectChatByReceive(Integer Receive);
    
    public List<Chat> selectChatBySendAndReceive(Integer Send, Integer Receive);

    //have write
    public void insertChatSelective(Chat chat);
}
