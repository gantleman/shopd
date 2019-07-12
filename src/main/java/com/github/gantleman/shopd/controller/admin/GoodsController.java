package com.github.gantleman.shopd.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.service.CateService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ImagePathService imagePathService;

    @RequestMapping("/showjson")
    @ResponseBody
    public Msg getAllGoods(@RequestParam(value = "page", defaultValue = "1") Integer pn, HttpServletResponse response, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("请先登录");
        }
        //一页显示几个数据
        PageHelper.startPage(pn, 10);

        List<Goods> employees = goodsService.selectByAll();

        //显示几个页号
        PageInfo page = new PageInfo(employees, 5);
        model.addAttribute("pageInfo", page);
        return Msg.success("查询成功!").add("pageInfo", page);
    }

    @RequestMapping("/show")
    public String goodsManage(@RequestParam(value = "page",defaultValue = "1") Integer pn, HttpServletResponse response, Model model, HttpSession session) throws IOException {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        List<Category> categoryList = cateService.selectByAll();
        model.addAttribute("categoryList",categoryList);

        return "adminAllGoods";
    }

    @RequestMapping("/add")
    public String showAdd(@ModelAttribute("succeseMsg") String msg, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        if(!msg.equals("")) {
            model.addAttribute("msg", msg);
        }

        List<Category> categoryList = cateService.selectByAll();
        model.addAttribute("categoryList",categoryList);

        //还需要查询分类传给addGoods页面
        return "addGoods";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Msg updateGoods(Goods goods, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("请先登录");
        }
       /* goods.setGoodsid(goodsid);*/
        goodsService.updateGoodsById(goods);
        return Msg.success("successful!");
    }

    @RequestMapping(value = "/delete/{goodsid}", method = RequestMethod.DELETE)
    @ResponseBody
    public Msg deleteGoods(@PathVariable("goodsid")Integer goodsid) {
        goodsService.deleteGoodsById(goodsid);
        return Msg.success("删除成功!");
    }

    @RequestMapping("/addGoodsSuccess")
    public String addGoods(Goods goods,
                           @RequestParam MultipartFile[] fileToUpload,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) throws IOException {

        /*goods.setCategory(1);*/
        goods.setUptime(new Date());
        goods.setActivityid(1);
        goodsService.insertGoods(goods);

        for(MultipartFile multipartFile:fileToUpload){
            if (multipartFile != null){

                String realPath = request.getSession().getServletContext().getRealPath("/");
                //图片路径=项目在本地磁盘的路径\shop\target\shop\shopimage
                String imageName = UUID.randomUUID().toString().replace("-", "") + multipartFile.getOriginalFilename();
                String imagePath = realPath.substring(0,realPath.indexOf("shop")) + "shopimage" + File.separatorChar + imageName;
                //把图片路径存入数据库中
                imagePathService.insertImagePath(new ImagePath(null, goods.getGoodsid(),imageName));
                //存图片
                multipartFile.transferTo(new File(imagePath));
            }
        }

        redirectAttributes.addFlashAttribute("succeseMsg","商品添加成功!");

        return "redirect:/admin/goods/add";
    }

    @RequestMapping("/addCategory")
    public String addcategory(@ModelAttribute("succeseMsg") String msg, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        List<Category> categoryList;
        categoryList = cateService.selectByAll();
        model.addAttribute("categoryList", categoryList);
        if (!msg.equals("")) {
            model.addAttribute("msg", msg);
        }
        return "addCategory";
    }

    @Autowired
    private CateService cateService;

    @RequestMapping("/addCategoryResult")
    public String addCategoryResult(Category category,Model addCategoryResult,RedirectAttributes redirectAttributes){
        List<Category> categoryList=new ArrayList<Category>();
        categoryList=cateService.selectByName(category.getCatename());
        if (!categoryList.isEmpty())
        {
            redirectAttributes.addAttribute("succeseMsg","分类已存在");
            return "redirect:/admin/goods/addCategory";
        }
        else {
            cateService.insertSelective(category);
            redirectAttributes.addFlashAttribute("succeseMsg","分类添加成功!");
            return "redirect:/admin/goods/addCategory";
        }
    }

    @RequestMapping("/saveCate")
    @ResponseBody
    public Msg saveCate(Category category){
        List<Category> categoryList=cateService.selectByName(category.getCatename());
        if (categoryList.isEmpty())
        {
            cateService.updateByPrimaryKeySelective(category);
            return Msg.success("successful");
        }
        else return Msg.success("名字已经存在");
    }

    @RequestMapping("/deleteCate")
    @ResponseBody
    public Msg deleteCate(Category category){
        cateService.deleteByPrimaryKey(category.getCateid());
        return Msg.success("删除成功");
    }
}
