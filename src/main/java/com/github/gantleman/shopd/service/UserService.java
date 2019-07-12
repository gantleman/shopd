package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.User;
import java.util.List;

public interface UserService {
    //only read
    public User selectByPrimaryKey(Integer userId);
    
    public List<User> selectByAll();
    
    public List<User> selectByInList(List<Integer> user);

    //have write
    public List<User> selectByName(String name);
    
    public List<User> selectByNameAndPasswrod(String name, String Passwrod);
    
    public void insertSelective(User user);

    public void deleteUserById(Integer userid);

    public void updateByPrimaryKeySelective(User user);

    public void TickBack();

    public void RefreshDBD();

}
