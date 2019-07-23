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
        categoryservice.Clean(false) ;
        return Msg.success("successful");
    }

    @RequestMapping("/catepage")
    public Msg catepage(Integer id){
        categoryservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/catepagename")
    public Msg catepagename(String name){
        categoryservice.RefreshDBD(name, true);
        return Msg.success("successful");
    }

    @RequestMapping("/cateclean")
    public Msg cateclean(){
        categoryservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
