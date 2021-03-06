package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.ChatDA;
import com.github.gantleman.shopd.da.ChatUserDA;
import com.github.gantleman.shopd.dao.ChatMapper;
import com.github.gantleman.shopd.entity.Chat;
import com.github.gantleman.shopd.entity.ChatExample;
import com.github.gantleman.shopd.entity.ChatUser;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ChatService;
import com.github.gantleman.shopd.service.jobs.ChatJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired(required = false)
    private ChatMapper chatMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private ChatJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Chat";

    private String classname_extra = "Chat_User";

    @PostConstruct
    public void init() {
      if (cacheService.IsCache(classname)) {
        ///create time
        quartzManager.addJob(classname,classname,classname,classname, ChatJob.class, null, job);
        }
    }

    @Override
    public Chat getChatByKey(Integer chatid, String url) {
        Chat re = null;
        Integer pageId = cacheService.PageID(chatid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Chat) redisu.hget(classname, chatid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/chatpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                re = (Chat) redisu.hget(classname, chatid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    @Override
    public List<Chat> selectChatBySend(Integer UserID, String url) {
        List<Chat> re = new ArrayList<Chat>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(UserID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
            if(ro!=null){
                for (Object id : ro) {
                    Chat r =  getChatByKey((Integer)id, url);
                    if (r != null && r.getSenduser() == UserID)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);                
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/chatuserpage", UserID);
            }else{
                RefreshUserDBD(UserID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(UserID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
                if(ro!=null){
                    for (Object id : ro) {
                        Chat r =  getChatByKey((Integer)id, url);
                        if (r != null && r.getSenduser() == UserID)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);                
                }
            }
        }
        return re;
    }

    @Override
    public List<Chat> selectChatByReceive(Integer UserID, String url) {
        List<Chat> re = new ArrayList<Chat>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(UserID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
            if(ro!=null){
                for (Object id : ro) {
                    Chat r =  getChatByKey((Integer)id, url);
                    if (r != null && r.getReceiveuser() == UserID)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);                
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/chatuserpage", UserID);
            }else{
                RefreshUserDBD(UserID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(UserID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
                if(ro!=null){
                    for (Object id : ro) {
                        Chat r =  getChatByKey((Integer)id, url);
                        if (r != null && r.getReceiveuser() == UserID)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);                
                }
            }
        }
        return re;
    }

    @Override
    public List<Chat> selectChatBySendAndReceive(Integer Send, Integer Receive, String url) {
        List<Chat> re = new ArrayList<Chat>();
        if(Send != Receive){
            List<Chat> rc = selectChatByReceive(Receive, url);
            for(Chat r: rc){
                if(r.getSenduser() == Send){
                    re.add(r);
                }
            }
        }
        return re;
    }

    public void insertSelective_extra(Chat chat) {
        //add to ChatUserDA
        RefreshUserDBD(chat.getSenduser(), false, false);
        RefreshUserDBD(chat.getReceiveuser(), false, false);

        BDBEnvironmentManager.getInstance();
        ChatUserDA chatUserDA=new ChatUserDA(BDBEnvironmentManager.getMyEntityStore());
        ChatUser senduser = chatUserDA.findChatUserById(chat.getSenduser());
        ChatUser receiveuser = chatUserDA.findChatUserById(chat.getReceiveuser());
        if(senduser == null) {
            senduser = new ChatUser();
        }

        senduser.addChatList(chat.getChatid());
        chatUserDA.saveChatUser(senduser);

        //Re-publish to redis
        redisu.sAdd("chat_u" + chat.getSenduser().toString(), chat.getChatid()); 

        if(receiveuser == null) {
            receiveuser = new ChatUser();
        }
        receiveuser.addChatList(chat.getChatid());
        chatUserDA.saveChatUser(receiveuser);

        //Re-publish to redis
        redisu.sAdd("chat_u" + chat.getSenduser().toString(), chat.getChatid());
        redisu.sAdd("chat_u" + chat.getReceiveuser().toString(), chat.getChatid());
    }

    @Override
    public void insertChatSelective(Chat chat) {
        BDBEnvironmentManager.getInstance();
        ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        chat.setChatid(new Long(id).intValue());
        chat.setStatus(CacheService.STATUS_INSERT);
        chatDA.saveChat(chat);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, chat.getChatid().toString(), (Object)chat, 0);

        insertSelective_extra(chat);
    }

    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ChatUserDA chatUserDA=new ChatUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ChatUser chatUser = chatUserDA.findChatUserById(i);
                if(chatUser != null){
                    chatUserDA.removedChatUserById(chatUser.getUserid());
                    redisu.del("chat_u"+chatUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (chatUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Chat chat = chatDA.findChatById(i);
                if(chat != null){
                    if(null ==  chat.getStatus()) {
                        chatDA.removedChatById(chat.getChatid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  chat.getStatus() && 1 == chatMapper.deleteByPrimaryKey(chat.getChatid())) {
                        chatDA.removedChatById(chat.getChatid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  chat.getStatus()  && 1 == chatMapper.insert(chat)) {
                        chatDA.removedChatById(chat.getChatid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  chat.getStatus() && 1 == chatMapper.updateByPrimaryKey(chat)) {
                        chatDA.removedChatById(chat.getChatid());
                    }
                    redisu.hdel(classname, chat.getChatid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (chatDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, ChatJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Chat> re = new ArrayList<Chat>();          
            ChatExample chatExample = new ChatExample();
            chatExample.or().andChatidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andChatidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = chatMapper.selectByExample(chatExample);
            for (Chat value : re) {
                redisu.hset(classname, value.getChatid().toString(), value);
                chatDA.saveChat(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Chat r = chatDA.findChatById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        ChatUserDA chatUserDA=new ChatUserDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<Chat> re = new ArrayList<Chat>();          
            ChatExample chatExample = new ChatExample();
            chatExample.or().andSenduserGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andSenduserLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            chatExample.or().andReceiveuserGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andReceiveuserLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = chatMapper.selectByExample(chatExample);
            for (Chat value : re) {
                ChatUser chatUser = chatUserDA.findChatUserById(value.getSenduser());
                if(chatUser == null){
                    chatUser = new ChatUser();
                }

                chatUser.addChatList(value.getChatid());

                redisu.sAdd("chat_u"+value.getSenduser().toString(), (Object)value.getChatid());

                if(andAll && userID == value.getSenduser()){
                    RefreshDBD(cacheService.PageID(value.getChatid()), refresRedis);
                }

                chatUserDA.saveChatUser(chatUser);
            }

            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    ChatUser r = chatUserDA.findChatUserById(i);
                    if(r!= null){
                        List<Integer> li = r.getChatList();
                        for(Integer chatid: li){
                          redisu.sAdd("chat_u"+r.getUserid().toString(), (Object)chatid);   
                        }  
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
