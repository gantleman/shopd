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
    public Msg orderitemtick(){
        orderitemservice.Clean(false) ;     
        return Msg.success("successful");
    }

    @RequestMapping("/orderitempage")
    public Msg orderitempage(Integer id){
        orderitemservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/orderitemuserpage")
    public Msg orderitemuserpage(Integer id){
        orderitemservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/orderitemclean")
    public Msg orderitemclean(){
        orderitemservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
