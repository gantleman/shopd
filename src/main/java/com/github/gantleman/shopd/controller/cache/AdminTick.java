package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.AdminService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminTick {
    @Autowired(required = false)
    private AdminService adminservice;

    @RequestMapping("/admintick")
    public Msg admintick(){
        adminservice.Clean(false) ;
        return Msg.success("successful");
    }

    @RequestMapping("/adminclean")
    public Msg adminclean(){
        adminservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
