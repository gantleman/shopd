package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.User;
import java.util.List;

public interface UserService {
    //only read
    public User selectByUserID(Integer userId, String url);
    
    public List<User> selectByAll(Integer pageid, String url);
    
    public List<User> selectByInList(List<Integer> user, String url);

    //have write
    public List<User> selectByName(String name);
    
    public List<User> selectByNameAndPasswrod(String name, String Passwrod);
    
    public void insertSelective(User user);

    public void deleteUserById(Integer userid);

    public void updateByPrimaryKeySelective(User user);

    public void Clean(Boolean all) ;

    public void RefreshDBD(Integer pageID, boolean refresRedis);
}
