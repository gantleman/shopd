package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.CateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CategoryTick {
    @Autowired(required = false)
    private CateService categoryservice;

    @RequestMapping("/categorytick")
    public Msg categorytick(){

        categoryservice.TickBack();
        
        return Msg.success("更新成功");
    }
}
