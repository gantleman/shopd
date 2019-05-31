package com.github.gantleman.shopd.service.impl;

import java.util.List;
import com.github.gantleman.shopd.dao.UserDao;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public List<User> selectUsers() {
        return userDao.findAllUser();
    }
}