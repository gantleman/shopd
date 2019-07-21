package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.ChatDA;
import com.github.gantleman.shopd.da.ChatUserDA;
import com.github.gantleman.shopd.dao.ChatMapper;
import com.github.gantleman.shopd.dao.ChatUserMapper;
import com.github.gantleman.shopd.entity.Chat;
import com.github.gantleman.shopd.entity.ChatExample;
import com.github.gantleman.shopd.entity.ChatUser;
import com.github.gantleman.shopd.entity.ChatUserExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.ChatService;
import com.github.gantleman.shopd.service.jobs.ChatJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired(required = false)
    private ChatMapper chatMapper;

    @Autowired(required = false)
    private ChatUserMapper chatUserMapper;

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

            if(redisu.hHasKey(classname, chatid.toString())) {
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

        if(redisu.hasKey("chat_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
            for (Object id : ro) {
                Chat r =  getChatByKey((Integer)id, url);
                if (r != null && r.getSenduser() == UserID)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/chatuserpage", UserID);
            }else{
                RefreshUserDBD(UserID, true, true);
            }

            if(redisu.hasKey("chat_u"+UserID.toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
                re = new ArrayList<Chat>();
                for (Object id : ro) {
                    Chat r =  getChatByKey((Integer)id, url);
                    if (r != null && r.getSenduser() == UserID)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
            }
        }
        return re;
    }

    @Override
    public List<Chat> selectChatByReceive(Integer UserID, String url) {
        List<Chat> re = new ArrayList<Chat>();

        if(redisu.hasKey("chat_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
            for (Object id : ro) {
                Chat r =  getChatByKey((Integer)id, url);
                if (r != null && r.getReceiveuser() == UserID)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/chatuserpage", UserID);
            }else{
                RefreshUserDBD(UserID, true, true);
            }

            if(redisu.hasKey("chat_u"+UserID.toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("chat_u"+UserID.toString());
                for (Object id : ro) {
                    Chat r =  getChatByKey((Integer)id, url);
                    if (r != null && r.getReceiveuser() == UserID)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(UserID).toString(), 1);
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
            List<Integer> chatIdList = new ArrayList<>();
            chatIdList.add(chat.getChatid());
            JSONArray jsonArray = JSONArray.fromObject(chatIdList);

            senduser = new ChatUser();
            senduser.setChatSize(1); 
            senduser.setChatList(jsonArray.toString());
            senduser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> chatIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(senduser.getChatList());
            chatIdList = JSONArray.toList(jsonArray,Integer.class);
            chatIdList.add(chat.getChatid());

            senduser.setChatSize(senduser.getChatSize() + 1); 
            senduser.setChatList(jsonArray.toString());
            if(senduser.getStatus() == null || senduser.getStatus() == CacheService.STATUS_DELETE)
            senduser.setStatus(CacheService.STATUS_UPDATE);
        }
        chatUserDA.saveChatUser(senduser);

        //Re-publish to redis
        redisu.sAdd("chat_u" + chat.getSenduser().toString(), chat.getChatid()); 

        if(receiveuser == null) {
            List<Integer> chatIdList = new ArrayList<>();
            chatIdList.add(chat.getChatid());
            JSONArray jsonArray = JSONArray.fromObject(chatIdList);

            receiveuser = new ChatUser();
            receiveuser.setChatSize(1); 
            receiveuser.setChatList(jsonArray.toString());
            receiveuser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> chatIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(receiveuser.getChatList());
            chatIdList = JSONArray.toList(jsonArray,Integer.class);
            chatIdList.add(chat.getChatid());

            receiveuser.setChatSize(receiveuser.getChatSize() + 1); 
            receiveuser.setChatList(jsonArray.toString());
            if(receiveuser.getStatus() == null || receiveuser.getStatus() == CacheService.STATUS_DELETE)
            receiveuser.setStatus(CacheService.STATUS_UPDATE);
        }
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
    public void TickBack_extra() {
        BDBEnvironmentManager.getInstance();
        ChatUserDA chatUserDA=new ChatUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                ChatUser chatUser = chatUserDA.findChatUserById(i);
                if(chatUser != null){
                    if(null ==  chatUser.getStatus()) {
                        chatUserDA.removedChatUserById(chatUser.getUserid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  chatUser.getStatus() && 1 == chatUserMapper.deleteByPrimaryKey(chatUser.getUserid())) {
                        chatUserDA.removedChatUserById(chatUser.getUserid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  chatUser.getStatus()  && 1 == chatUserMapper.insert(chatUser)) {
                        chatUserDA.removedChatUserById(chatUser.getUserid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  chatUser.getStatus() && 1 == chatUserMapper.updateByPrimaryKey(chatUser)) {
                        chatUserDA.removedChatUserById(chatUser.getUserid());
                    }
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
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
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

        TickBack_extra();
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, ChatJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            ChatDA chatDA=new ChatDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Chat> re = new ArrayList<Chat>();          
            ChatExample chatExample = new ChatExample();
            chatExample.or().andChatidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            chatExample.or().andChatidLessThanOrEqualTo(cacheService.PageEnd(pageID));

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
            List<ChatUser> re = new ArrayList<ChatUser>();          
            ChatUserExample chatUserExample = new ChatUserExample();
            chatUserExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)));
            chatUserExample.or().andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = chatUserMapper.selectByExample(chatUserExample);
            for (ChatUser value : re) {
                chatUserDA.saveChatUser(value);

                List<Integer> chatIdList = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(value.getChatList());
                chatIdList = JSONArray.toList(jsonArray, Integer.class);

                for(Integer chatId: chatIdList){
                    redisu.sAdd("chat_u"+value.getUserid().toString(), (Object)chatId);
                }

                if(andAll && userID == value.getUserid() && value.getChatSize() != 0){  
                    for(Integer chatId: chatIdList){
                        RefreshDBD(cacheService.PageID(chatId), refresRedis);
                    }
                }
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    ChatUser r = chatUserDA.findChatUserById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.sAdd("chat_u"+r.getUserid().toString(), (Object)r.getChatList()); 
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }
}
