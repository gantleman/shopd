package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrderTick {
    @Autowired(required = false)
    private OrderService orderservice;

    @RequestMapping("/ordertick")
    public Msg goodstick(){
        orderservice.TickBack();
        return Msg.success("successful");
    }

    @RequestMapping("/orderpage")
    public Msg orderpage(Integer id){
        orderservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/orderuserpage")
    public Msg orderuserpage(Integer id){
        orderservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/orderispage")
    public Msg orderispage(Integer id){
        orderservice.RefreshIsDBD(true);
        return Msg.success("successful");
    }
}
