package com.github.gantleman.shopd.controller.cache;

import com.github.gantleman.shopd.entity.Msg;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CacheController {
    @RequestMapping("/cache")
    public Msg cache(){

        return Msg.success("更新成功");
    }
}
