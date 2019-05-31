package com.github.gantleman.shopd.service;

import java.util.List;
import com.github.gantleman.shopd.entity.User;

public interface UserService {
    List<User> selectUsers();
}