package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.AdminService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
    @Autowired(required = false)
    private AdminService adminservice;

    @RequestMapping("/admintick")
    public Msg admintick(){

        adminservice.SaveBack();
        
        return Msg.success("更新成功");
    }
}
