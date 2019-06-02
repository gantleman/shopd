package com.github.gantleman.shopd.controller;

import java.util.List;

import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/myb")
public class UserController {

    @Autowired
    UserService xyUserService;

    @GetMapping("/t")
    public ResponseEntity<?> getUsers(){
        List<User> users = xyUserService.selectUsers();
        return ResponseEntity.ok(users);
    }
}