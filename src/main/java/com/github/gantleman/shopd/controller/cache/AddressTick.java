package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AddressTick {
    @Autowired(required = false)
    private ActivityService addressservice;

    @RequestMapping("/addresstick")
    public Msg addresstick(){

        addressservice.TickBack();
        
        return Msg.success("successful");
    }
}
