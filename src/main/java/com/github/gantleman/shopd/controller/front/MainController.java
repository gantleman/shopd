package com.github.gantleman.shopd.controller.front;

import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.service.CateService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CateService cateService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ImagePathService imagePathService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/main")
    public String showAllGoods(Model model, HttpSession session) {

        Integer userid;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            userid = null;
        } else {
            userid = user.getUserid();
        }

        //Digital Classification
        List<Goods> digGoods = getCateGoods("Digital", userid);
        model.addAttribute("digGoods", digGoods);

        //household electrical appliances
        List<Goods> houseGoods = getCateGoods("Appliances", userid);
        model.addAttribute("houseGoods", houseGoods);

        //Clothes Accessories
        List<Goods> colGoods = getCateGoods("Clothes", userid);
        model.addAttribute("colGoods", colGoods);

        //book
        List<Goods> bookGoods = getCateGoods("Book", userid);
        model.addAttribute("bookGoods", bookGoods);

        return "main";
    }

    public List<Goods> getCateGoods(String cate, Integer userid) {
        //Query classification
        List<Category> digCategoryList = cateService.selectByName(cate);

        if (digCategoryList.size() == 0) {
            return null;
        }

        //Search for goods that belong to the category just found
        List<Integer> digCateId = new ArrayList<Integer>();
        for (Category tmp:digCategoryList) {
            digCateId.add(tmp.getCateid());
        }

        List<Goods> goodsList = goodsService.selectByCateLimit(digCateId, request.getServletPath());

        List<Goods> goodsAndImage = new ArrayList<Goods>();
        //Get a picture of each item
        for (Goods goods:goodsList) {
            //Determine whether it is a login state
            if (userid == null) {
                goods.setFav(false);
            } else {
                Favorite favorite = favoriteService.selectFavByKey(userid, goods.getGoodsid(), request.getServletPath());
                if (favorite == null) {
                    goods.setFav(false);
                } else {
                    goods.setFav(true);
                }
            }

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid(),request.getServletPath());
            goods.setImagePaths(imagePathList);
            goodsAndImage.add(goods);
        }
        return goodsAndImage;
    }
}
