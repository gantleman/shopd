package com.github.gantleman.shopd.dao;

import java.util.List;
import com.github.gantleman.shopd.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    List<User> findAllUser();
}