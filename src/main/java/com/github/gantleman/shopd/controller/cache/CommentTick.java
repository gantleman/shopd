package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommentTick {
    @Autowired(required = false)
    private CommentService commentservice;

    @RequestMapping("/commnettick")
    public Msg activitytick(){

        commentservice.TickBack();
        
        return Msg.success("successful");
    }

    @RequestMapping("/commentpage")
    public Msg commentpage(Integer id){
        commentservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/commentgoodspage")
    public Msg commentgoodspage(Integer id){
        commentservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }
}
