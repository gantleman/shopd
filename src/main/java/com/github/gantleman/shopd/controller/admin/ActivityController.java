package com.github.gantleman.shopd.controller.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.ActivityService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/activity")
public class ActivityController {
    @Autowired
    private HttpServletRequest request;

    @Autowired(required = false)
    ActivityService activityService;

    @Autowired(required = false)
    GoodsService goodsService;

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/show")
    public String showActivity(@RequestParam(value = "page",defaultValue = "1") Integer pn, Model model, HttpSession session) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());

        List<Activity> activityList = activityService.getAllActivity(pn-1, request.getServletPath());

        //Display several page numbers
        PageInfo page = new PageInfo(activityList, 5);
        model.addAttribute("pageInfo", page);

        return "activity";
    }

    @RequestMapping("/showjson")
    @ResponseBody
    public Msg showActivityJson(@RequestParam(value = "page",defaultValue = "1") Integer pn, Model model ,HttpSession session) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("Please login first");
        }
        
        List<Activity> activityList = activityService.getAllActivity(pn-1, request.getServletPath());

        return Msg.success("Access to Activity Information Successfully").add("activity",activityList);
    }

    @RequestMapping("/add")
    public String showAddActivity(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        return "addActivity";
    }

    @RequestMapping("/addResult")
    public String addActivity(Activity activity) {

        activityService.insertActivitySelective(activity);

        return "redirect:/admin/activity/show";
    }

    @RequestMapping("/update")
    @ResponseBody
    public Msg updateActivity(Integer goodsid, Integer activityid, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("Please login first");
        }
        Goods goods = new Goods();
        goods.setActivityid(activityid);
        goods.setGoodsid(goodsid);
        goodsService.updateGoodsById(goods);
        return Msg.success("Successful Renewal of Commodity Activities");
    }

    @RequestMapping("delete")
    public String deleteActivity(Integer activityid, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        activityService.deleteByActivityId(activityid);
        return "redirect:/admin/activity/show";
    }
}
