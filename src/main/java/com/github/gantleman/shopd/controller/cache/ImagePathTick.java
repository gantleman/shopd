package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ImagePathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ImagePathTick {
    @Autowired(required = false)
    private ImagePathService imagepathservice;

    @RequestMapping("/imagepathtick")
    public Msg goodstick(){
        imagepathservice.Clean(false) ;
        return Msg.success("successful");
    }

    @RequestMapping("/imagepathpage")
    public Msg imagepathpage(Integer id){
        imagepathservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/imagepathuserpage")
    public Msg imagepathuserpage(Integer id){
        imagepathservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/imagepathclean")
    public Msg imagepathclean(){
        imagepathservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
