package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.OrderItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrderItemTick {
    @Autowired(required = false)
    private OrderItemService orderitemservice;

    @RequestMapping("/orderitemtick")
    public Msg goodstick(){

        orderitemservice.TickBack();
        
        return Msg.success("successful");
    }
}
