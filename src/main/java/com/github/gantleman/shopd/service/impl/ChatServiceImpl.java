package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.ChatDA;
import com.github.gantleman.shopd.dao.ChatMapper;
import com.github.gantleman.shopd.entity.Chat;
import com.github.gantleman.shopd.entity.ChatExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ChatService;
import com.github.gantleman.shopd.service.jobs.ChatJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired(required = false)
    ChatMapper chatMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private ChatJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Chat";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
      if (cacheService.IsCache(classname)) {
        ///create time
        quartzManager.addJob(classname,classname,classname,classname, ChatJob.class, null, job);
        }
    }

    @Override
    public List<Chat> selectChatBySend(Integer Send) {

        List<Chat> re = new ArrayList<>();
        if(redisu.hasKey("Chat_u"+Send.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Chat_u"+Send.toString());
            re = new ArrayList<Chat>();
            for (Object id : ro) {
                Chat r =  (Chat) redisu.hget(classname, ((Integer)id).toString());
                if (r != null && r.getSenduser() == Send)
                    re.add(r);
            }
            redisu.expire("Chat_u"+Send.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            ChatExample chatExample=new ChatExample();
            chatExample.or().andSenduserEqualTo(Send);
            chatExample.or().andReceiveuserEqualTo(Send);
            
            List<Chat> lre = chatMapper.selectByExample(chatExample);

            ///read and write
            if(!redisu.hasKey("Chat_u"+Send.toString())) {
                for( Chat item : lre ){
                    redisu.sAdd("Chat_u"+Send.toString(), (Object)item.getChatid());
                    redisu.hset(classname, item.getChatid().toString(), item);

                    if (item != null && item.getSenduser() == Send)
                    re.add(item);
                }
                redisu.expire("Chat_u"+Send.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Chat> selectChatByReceive(Integer Receive) {

        List<Chat> re = new ArrayList<>();
        if(redisu.hasKey("Chat_u"+Receive.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Chat_u"+Receive.toString());
            re = new ArrayList<Chat>();
            for (Object id : ro) {
                Chat r =  (Chat) redisu.hget(classname, ((Integer)id).toString());
                if (r != null && r.getReceiveuser() == Receive)
                    re.add(r);
            }
            redisu.expire("Chat_u"+Receive.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            ChatExample chatExample=new ChatExample();
            chatExample.or().andSenduserEqualTo(Receive);
            chatExample.or().andReceiveuserEqualTo(Receive);
            
            List<Chat> lre = chatMapper.selectByExample(chatExample);

            ///read and write
            if(!redisu.hasKey("Chat_u"+Receive.toString())) {
                for( Chat item : lre ){
                    redisu.sAdd("Chat_u"+Receive.toString(), (Object)item.getChatid());
                    redisu.hset(classname, item.getChatid().toString(), item);

                    if (item != null && item.getReceiveuser() == Receive)
                        re.add(item);
                }
                redisu.expire("Chat_u"+Receive.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }

    @Override
    public List<Chat> selectChatBySendAndReceive(Integer Send, Integer Receive) {
        List<Chat> re = new ArrayList<>();
        if(redisu.hasKey("Chat_u"+Receive.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Chat_u"+Receive.toString());
            re = new ArrayList<Chat>();
            for (Object id : ro) {
                Chat r =  (Chat) redisu.hget(classname, ((Integer)id).toString());
                if (r != null && ((r.getSenduser() == Send && r.getReceiveuser() == Receive)
                ||  (r.getSenduser() == Receive && r.getReceiveuser() == Send)))
                    re.add(r);
            }
            redisu.expire("Chat_u"+Receive.toString(), 0);
            redisu.expire(classname, 0);
        }else {
            //write redis
            ChatExample chatExample=new ChatExample();
            chatExample.or().andSenduserEqualTo(Send);
            chatExample.or().andReceiveuserEqualTo(Send);
            
            List<Chat> lre = chatMapper.selectByExample(chatExample);

            ///read and write
            if(!redisu.hasKey("Chat_u"+Receive.toString())) {
                for( Chat item : lre ){
                    redisu.sAdd("Chat_u"+Receive.toString(), (Object)item.getChatid());
                    redisu.hset(classname, item.getChatid().toString(), item);

                    if (((item.getSenduser() == Send && item.getReceiveuser() == Receive)
                    ||  (item.getSenduser() == Receive && item.getReceiveuser() == Send)))
                        re.add(item);
                }
                redisu.expire("Chat_u"+Receive.toString(), 0);
                redisu.expire(classname, 0);
            }   
        }
        return re;
    }


    @Override
    public void insertChatSelective(Chat chat) {
        //send do
        RefreshDBD(chat.getSenduser());

        BDBEnvironmentManager.getInstance();
        ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        chat.setChatid(new Long(id).intValue());
        chat.MakeStamp();
        chat.setStatus(2);
        chatDA.saveChat(chat);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Chat_u" + chat.getSenduser().toString(), 0, chat.getChatid()); 
        redisu.hset(classname, chat.getChatid().toString(), (Object)chat, 0);
    }


    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());
        List<Chat> lchat = chatDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Chat chat : lchat) {
            if(null ==  chat.getStatus()) {
                chatDA.removedChatById(chat.getChatid());
            }

            if(2 ==  chat.getStatus()  && 1 == chatMapper.insert(chat)) {
                chatDA.removedChatById(chat.getChatid());
            }
        }

        if (chatDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD(Integer userid) {
        ///init
       if (cacheService.IsCache(classname, userid)) {
           BDBEnvironmentManager.getInstance();
           ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());

           List<Chat> re = new ArrayList<Chat>();

           ChatExample chatExample = new ChatExample();
           chatExample.or().andReceiveuserEqualTo(userid);
           chatExample.or().andSenduserEqualTo(userid);
           chatExample.setOrderByClause("MsgTime asc");

           re = chatMapper.selectByExample(chatExample);
           for (Chat value : re) {
               value.MakeStamp();
               chatDA.saveChat(value);

               redisu.sAddAndTime("Chat_u"+userid.toString(), 0, value.getChatid()); 
               redisu.hset(classname, value.getChatid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, ChatJob.class, null, job);          
           }
       }
   }
}
