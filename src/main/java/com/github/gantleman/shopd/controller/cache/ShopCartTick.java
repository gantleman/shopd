package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ShopCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShopCartTick {
    @Autowired(required = false)
    private ShopCartService shopcartservice;

    @RequestMapping("/shopcarttick")
    public Msg goodstick(){

        shopcartservice.TickBack();
        
        return Msg.success("successful");
    }
}
