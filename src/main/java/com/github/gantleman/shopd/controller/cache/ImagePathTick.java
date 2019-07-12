package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ImagePathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ImagePathTick {
    @Autowired(required = false)
    private ImagePathService imagepoathservice;

    @RequestMapping("/imagepathtick")
    public Msg goodstick(){

        imagepoathservice.TickBack();
        
        return Msg.success("successful");
    }
}
