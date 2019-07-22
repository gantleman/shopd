package com.github.gantleman.shopd.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.CateService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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

@Controller
@RequestMapping("/admin/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ImagePathService imagePathService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/showjson")
    @ResponseBody
    public Msg getAllGoods(@RequestParam(value = "page", defaultValue = "1") Integer pn, HttpServletResponse response, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("Please login first");
        }
        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());

        List<Goods> employees = goodsService.selectByAll(pn-1, request.getServletPath());

        //Display several page numbers
        PageInfo page = new PageInfo(employees, 5);
        model.addAttribute("pageInfo", page);
        return Msg.success("query was successful!").add("pageInfo", page);
    }

    @RequestMapping("/show")
    public String goodsManage(@RequestParam(value = "page",defaultValue = "1") Integer pn, HttpServletResponse response, Model model, HttpSession session) throws IOException {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        List<Category> categoryList = cateService.selectByAll(pn -1, request.getServletPath());
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

        List<Category> categoryList = cateService.selectByAll(0, request.getServletPath());
        model.addAttribute("categoryList",categoryList);

        //You also need to query categories to pass to the addGoods page
        return "addGoods";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Msg updateGoods(Goods goods, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return Msg.fail("Please login first");
        }
       /* goods.setGoodsid(goodsid);*/
        goodsService.updateGoodsById(goods);
        return Msg.success("successful!");
    }

    @RequestMapping(value = "/delete/{goodsid}", method = RequestMethod.DELETE)
    @ResponseBody
    public Msg deleteGoods(@PathVariable("goodsid")Integer goodsid) {
        goodsService.deleteGoodsById(goodsid);
        return Msg.success("Successful deletion!");
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
                //Picture path = project path on local diskshoptargetshopshopimage
                String imageName = UUID.randomUUID().toString().replace("-", "") + multipartFile.getOriginalFilename();
                String imagePath = realPath.substring(0,realPath.indexOf("shop")) + "shopimage" + File.separatorChar + imageName;
                //Store the picture path in the database
                imagePathService.insertImagePath(new ImagePath(null, goods.getGoodsid(),imageName));
                //Save pictures
                multipartFile.transferTo(new File(imagePath));
            }
        }

        redirectAttributes.addFlashAttribute("succeseMsg","Successful merchandise addition!");

        return "redirect:/admin/goods/add";
    }

    @RequestMapping("/addCategory")
    public String addcategory(@ModelAttribute("succeseMsg") String msg, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        List<Category> categoryList;
        categoryList = cateService.selectByAll(0, request.getServletPath());
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
            redirectAttributes.addAttribute("succeseMsg","Category already exists");
            return "redirect:/admin/goods/addCategory";
        }
        else {
            cateService.insertSelective(category);
            redirectAttributes.addFlashAttribute("succeseMsg","Category Added Successfully!");
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
        else return Msg.success("Names already exist");
    }

    @RequestMapping("/deleteCate")
    @ResponseBody
    public Msg deleteCate(Category category){
        cateService.deleteByPrimaryKey(category.getCateid());
        return Msg.success("Successful deletion");
    }
}
