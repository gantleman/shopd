package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.GoodsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GoodsTick {
    @Autowired(required = false)
    private GoodsService goodsservice;

    @RequestMapping("/goodstick")
    public Msg goodstick(){

        goodsservice.TickBack();
        
        return Msg.success("successful");
    }
}
