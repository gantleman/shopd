package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 文辉 on 2017/7/26.
 */
@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired(required = false)
    ChatMapper chatMapper;

    @Override
    public void insertChatSelective(Chat chat) {
        chatMapper.insertSelective(chat);
    }

    @Override
    public List<Chat> selectChatBySend(Integer Send) {

        ChatExample chatExample = new ChatExample();
        chatExample.or().andSenduserEqualTo(Send);

        return chatMapper.selectByExample(chatExample);
    }

    @Override
    public List<Chat> selectChatByReceive(Integer Receive) {

        ChatExample chatExample = new ChatExample();
        chatExample.or().andReceiveuserEqualTo(Receive);

        return chatMapper.selectByExample(chatExample);
    }

    @Override
    public List<Chat> selectChatBySendAndReceive(Integer Send, Integer Receive) {
        ChatExample chatExample = new ChatExample();
        chatExample.or().andReceiveuserEqualTo(Send).andSenduserEqualTo(Receive);
        chatExample.or().andSenduserEqualTo(Send).andReceiveuserEqualTo(Receive);
        chatExample.setOrderByClause("MsgTime asc");

        return chatMapper.selectByExample(chatExample);
    }
}
