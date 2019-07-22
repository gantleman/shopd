package com.github.gantleman.shopd.controller.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/showjson")
    @ResponseBody
    public Msg getAllGoods(@RequestParam(value = "page",defaultValue = "1") Integer pn, HttpServletResponse response, Model model) {
        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());

        List<User> userList = userService.selectByAll(pn-1, request.getServletPath());

        //Display several page numbers
        PageInfo page = new PageInfo(userList,5);
        //model.addAttribute("pageInfo", page);

        return Msg.success("query was successful!").add("pageInfo", page);
    }

    @RequestMapping("/show")
    public String userManage() {
        return "userManage";
    }

    @RequestMapping(value = "/delete/{userid}", method = RequestMethod.DELETE)
    @ResponseBody
    public Msg deleteUser(@PathVariable("userid")Integer userid) {
//        goodsService.deleteGoodsById(goodsid);
        userService.deleteUserById(userid);
        return Msg.success("Successful deletion!");
    }
}
