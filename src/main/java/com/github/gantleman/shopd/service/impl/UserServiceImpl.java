package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.UserDA;
import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import com.github.gantleman.shopd.service.jobs.UserJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private UserJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "User";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, UserJob.class, null, job);
        }
    }

    @Override
    public User selectByUserID(Integer userId) {
        User re = (User) redisu.hget(classname, userId.toString());
        if(re == null){
            if(redisu.hasKey(classname)){
                //write redis
                Map<String, Object> tmap = new HashMap<>();

                List<User> lre = userMapper.selectByExample(new UserExample());
                for (User value : lre) {
                    tmap.put(value.getUserid().toString(), (Object)value);
                }

                ///read and write
                if(!redisu.hasKey(classname)) {
                    redisu.hmset(classname, tmap, 0);
                }   
            }
        }

        return re;
    }

    @Override
    public List<User> selectByAll() {
        List<User> re = new ArrayList<User>();

        if(redisu.hasKey(classname)) {
            //read redis
            Map<Object, Object> rm = redisu.hmget(classname);
            for (Object value : rm.values()) {
                re.add( (User)value);
            }

            redisu.expire(classname, 0);
        }else {
            //write redis
            Map<String, Object> tmap = new HashMap<>();

            re = userMapper.selectByExample(new UserExample());
            for (User value : re) {
                tmap.put(value.getUserid().toString(), (Object)value);
            }

            ///read and write
            if(!redisu.hasKey(classname)) {
                redisu.hmset(classname, tmap, 0);
            }   
        }
        return re;
    }

    @Override
    public List<User> selectByInList(List<Integer> user) {
        List<User> re = new ArrayList<User>();
        for(Integer id : user){
            User r = selectByUserID(id);
            if(r != null)
                re.add(r);
        }

        return re;
    }

    @Override
    public List<User> selectByName(String name) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

        return userDA.findAllUserByUserName(name);
    }

    @Override
    public List<User> selectByNameAndPasswrod(String name, String Passwrod) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

        return userDA.findAllUserByUserNameAndPassword(name, Passwrod);

    }

    @Override
    public void insertSelective(User user) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        user.setUserid(new Long(id).intValue());
        user.MakeStamp();
        user.setStatus(2);
        userDA.saveUser(user);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, user.getUserid().toString(), user, 0);
    }

    @Override
    public void deleteUserById(Integer userid) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        User user = userDA.findUserById(userid);
 
        if (user != null)
        {
             user.MakeStamp();
             user.setStatus(1);
             userDA.saveUser(user);
 
             //Re-publish to redis
             redisu.hdel(classname, user.getUserid().toString());
        } 
    }

    @Override
    public void updateByPrimaryKeySelective(User iuser) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        User user = userDA.findUserById(iuser.getUserid());
 
        if (user != null)
        {
            iuser.MakeStamp();
            iuser.setStatus(3);
            
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
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());
        List<User> luser = userDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (User user : luser) {
            if(null ==  user.getStatus()) {
                userDA.removedUserById(user.getUserid());
            }

            if(1 ==  user.getStatus() && 1 == userMapper.deleteByPrimaryKey(user.getUserid  ())) {
                userDA.removedUserById(user.getUserid());
            }

            if(2 ==  user.getStatus()  && 1 == userMapper.insert(user)) {
                userDA.removedUserById(user.getUserid());
            }

            if(3 ==  user.getStatus() && 1 == userMapper.updateByPrimaryKey(user)) {
                userDA.removedUserById(user.getUserid());
            }
        }

        if (userDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        ///init
       if (cacheService.IsCache(classname)) {
           BDBEnvironmentManager.getInstance();
           UserDA userDA=new UserDA(BDBEnvironmentManager.getMyEntityStore());

           List<User> re = new ArrayList<User>();
           re = userMapper.selectByExample(new UserExample());
           for (User value : re) {
                value.MakeStamp();
                userDA.saveUser(value);

                redisu.hset(classname, value.getUserid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, UserJob.class, null, job);          
           }
       }
   }
}
