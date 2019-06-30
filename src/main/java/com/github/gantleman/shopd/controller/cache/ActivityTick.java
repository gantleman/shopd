package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ActivityTick {
    @Autowired(required = false)
    private ActivityService activityservice;

    @RequestMapping("/activitytick")
    public Msg activitytick(){

        activityservice.TickBack();
        
        return Msg.success("更新成功");
    }
}
