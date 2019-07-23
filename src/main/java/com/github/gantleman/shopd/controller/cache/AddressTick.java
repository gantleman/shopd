package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AddressTick {
    @Autowired(required = false)
    private AddressService addressservice;

    @RequestMapping("/addresstick")
    public Msg addresstick(){
        addressservice.Clean(false) ;     
        return Msg.success("successful");
    }

    @RequestMapping("/addresspage")
    public Msg addresspage(Integer id){
        addressservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/addressuserpage")
    public Msg addressuserpage(Integer id){
        addressservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/addressclean")
    public Msg addressclean(){
        addressservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
