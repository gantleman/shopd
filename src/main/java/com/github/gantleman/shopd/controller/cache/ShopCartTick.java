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
    public Msg shopcarttick(){
        shopcartservice.Clean(false) ;
        return Msg.success("successful");
    }


    @RequestMapping("/shopcartpage")
    public Msg shopcartpage(Integer id){
        shopcartservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/shopcartuserpage")
    public Msg shopcartuserpage(Integer id){
        shopcartservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/shopcartclean")
    public Msg shopcartclean(){
        shopcartservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
