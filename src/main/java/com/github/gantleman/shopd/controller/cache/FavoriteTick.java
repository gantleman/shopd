package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FavoriteTick {
    @Autowired(required = false)
    private FavoriteService favoriteservice;

    @RequestMapping("/favoritetick")
    public Msg activitytick(){

        favoriteservice.TickBack();
        
        return Msg.success("successful");
    }
}
