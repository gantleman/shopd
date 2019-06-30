package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.*;
import com.github.gantleman.shopd.entity.*;
import com.github.gantleman.shopd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 蒋松冬 on 2017/7/22.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Override
    public User selectByPrimaryKey(int userId) {
        return  userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<User> selectByAll() {
        return userMapper.selectByExample(new UserExample());
    }

    @Override
    public List<User> selectByName(String name) {
        UserExample userExample=new UserExample();
        userExample.or().andUsernameLike(name);

        return userMapper.selectByExample(userExample);
    }

    @Override
    public List<User> selectByInList(List<Integer> user) {

        UserExample userExample = new UserExample();
        userExample.or().andUseridIn(user);

        return userMapper.selectByExample(userExample);
    }

    @Override
    public List<User> selectByNameAndPasswrod(String name, String Passwrod) {
        UserExample userExample=new UserExample();
        userExample.or().andUsernameEqualTo(name).andPasswordEqualTo(Passwrod);

        return userMapper.selectByExample(userExample);
    }

    @Override
    public void insertSelective(User user) {
        userMapper.insertSelective(user);
    }

    @Override
    public void deleteUserById(Integer userid) {
        userMapper.deleteByPrimaryKey(userid);
    }

    @Override
    public void updateByPrimaryKeySelective(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }


   /* @Override
    public User selectByPrimaryKeyAndPassword(int userId,String password){return userMapper.selectByPrimaryKeyAndPassword();}*/
}
