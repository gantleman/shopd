package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserTick {
    @Autowired(required = false)
    private UserService userservice;

    @RequestMapping("/usertick")
    public Msg goodstick(){

        userservice.TickBack();
        
        return Msg.success("successful");
    }
}
