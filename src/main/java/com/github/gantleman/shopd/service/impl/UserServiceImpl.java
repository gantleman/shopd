package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.UserDA;
import com.github.gantleman.shopd.dao.UserMapper;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.entity.UserExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.UserService;
import com.github.gantleman.shopd.service.jobs.UserJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private UserJob job;
    
    @Autowired
    private QuartzManager quartzManager;

    private String classname = "User";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, UserJob.class, null, job);
        }
    }

    @Override
    public User selectByUserID(Integer userid, String url) {
        User re = null;
        Integer pageId = cacheService.PageID(userid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (User) redisu.hget(classname, userid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/userpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
                //read redis
                re = (User) redisu.hget(classname, userid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }  
        }
        return re;
    }

    @Override
    public List<User> selectByAll(Integer pageId, String url) {
        List<User> re = new ArrayList<User>();

        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                User r = (User) redisu.hget(classname, pageId.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/userpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                User r = (User) redisu.hget(classname, i.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }
        return re;
    }

    @Override
    public List<User> selectByInList(List<Integer> user, String url) {
        List<User> re = new ArrayList<User>();
        for(Integer id : user){
            User r = selectByUserID(id, url);
            if(r != null)
                re.add(r);
        }

        return re;
    }

    @Override
    public List<User> selectByName(String name) {
        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        List<User> user = userDA.findAllUserByUserName(name);
        if(user.isEmpty()){
            UserExample userExample = new UserExample();
            userExample.or().andUsernameEqualTo(name);
            user = userMapper.selectByExample(userExample);
            
            for(User u : user){
                RefreshDBD(cacheService.PageID(u.getUserid()), false);
            }
        }
        return user;
    }

    @Override
    public List<User> selectByNameAndPasswrod(String name, String Passwrod) {
        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        List<User> user = userDA.findAllUserByUserNameAndPassword(name, Passwrod);
        if(user.isEmpty()){
            UserExample userExample = new UserExample();
            userExample.or().andUsernameEqualTo(name)
            .andPasswordEqualTo(Passwrod);
            user = userMapper.selectByExample(userExample);
            
            for(User u : user){
                RefreshDBD(cacheService.PageID(u.getUserid()), false);
            }
        }
        return user;

    }

    @Override
    public void insertSelective(User user) {

        RefreshDBD(cacheService.PageID(user.getUserid()), false);

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        user.setUserid(new Long(id).intValue());
        user.setStatus(CacheService.STATUS_INSERT);
        userDA.saveUser(user);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset("user_u", user.getUsername(), user.getUserid());
        redisu.hset(classname, user.getUserid().toString(), user, 0);
    }

    @Override
    public void deleteUserById(Integer userid) {
        RefreshDBD(cacheService.PageID(userid), false);

       BDBEnvironmentManager.getInstance();
       UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
       User user = userDA.findUserById(userid);

       if(user != null && user.getStatus() == CacheService.STATUS_INSERT){
            userDA.removedUserById(userid);
            //Re-publish to redis
            redisu.hdel(classname, user.getUserid().toString());
            redisu.hdel("user_u", user.getUsername());
       } else if (user != null)
       {
            user.setStatus(CacheService.STATUS_DELETE);
            userDA.saveUser(user);

            //Re-publish to redis
            redisu.hdel("user_u", user.getUsername());
            redisu.hdel(classname, user.getUserid().toString());
       }   
    }

    @Override
    public void updateByPrimaryKeySelective(User iuser) {
        RefreshDBD(cacheService.PageID(iuser.getUserid()), false);

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        User user = userDA.findUserById(iuser.getUserid());
 
        if (user != null)
        {
            if(iuser.getStatus()== null){
                iuser.setStatus(CacheService.STATUS_UPDATE);
            }
            
            if(iuser.getEmail() != null){
                user.setEmail(iuser.getEmail());
            }
            if(iuser.getPassword() != null){
                user.setPassword(iuser.getPassword());
            }
            if(iuser.getTelephone() != null){
                user.setTelephone(iuser.getTelephone());

            }
            if(iuser.getUsername() != null){
                user.setUsername(iuser.getUsername());
            }
            userDA.saveUser(user);

            //Re-publish to redis
            redisu.hset(classname, user.getUserid().toString(), (Object)user, 0);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                User user = userDA.findUserById(i);
                if(user != null){
                    if(null ==  user.getStatus()) {
                        userDA.removedUserById(user.getUserid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  user.getStatus() && 1 == userMapper.deleteByPrimaryKey(user.getUserid())) {
                        userDA.removedUserById(user.getUserid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  user.getStatus()  && 1 == userMapper.insert(user)) {
                        userDA.removedUserById(user.getUserid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  user.getStatus() && 1 == userMapper.updateByPrimaryKey(user)) {
                        userDA.removedUserById(user.getUserid());
                    } 
                    redisu.hdel("user_u", user.getUsername());
                    redisu.hdel(classname, user.getUserid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (userDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, UserJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<User> re = new ArrayList<User>();          
            UserExample userExample = new UserExample();
            userExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andUseridLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = userMapper.selectByExample(userExample);
            for (User value : re) {
                redisu.hset(classname, value.getUserid().toString(), value);
                userDA.saveUser(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){

                    User r = userDA.findUserById(i);
                    if(r != null && r.getStatus() != CacheService.STATUS_DELETE)
                     redisu.hset(classname, i.toString(), r);                        
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }
}
