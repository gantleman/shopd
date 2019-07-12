package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ChatTick {
    @Autowired(required = false)
    private ChatService chatservice;

    @RequestMapping("/chattick")
    public Msg activitytick(){

        chatservice.TickBack();
        
        return Msg.success("successful");
    }
}
