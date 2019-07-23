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
        favoriteservice.Clean(false) ;     
        return Msg.success("successful");
    }

    @RequestMapping("/favoritepage")
    public Msg favoritepage(Integer id){
        favoriteservice.RefreshDBD(id, true);
        return Msg.success("successful");
    }

    @RequestMapping("/favoriteuserpage")
    public Msg favoriteuserpage(Integer id){
        favoriteservice.RefreshUserDBD(id, true, true);
        return Msg.success("successful");
    }

    @RequestMapping("/favoriteclean")
    public Msg favoriteclean(){
        favoriteservice.Clean(true) ;   
        return Msg.success("successful");
    }
}
