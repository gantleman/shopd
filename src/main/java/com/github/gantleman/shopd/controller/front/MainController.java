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

    @RequestMapping("/main")
    public String showAllGoods(Model model, HttpSession session) {

        Integer userid;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            userid = null;
        } else {
            userid = user.getUserid();
        }

        //数码分类
        List<Goods> digGoods = getCateGoods("数码", userid);
        model.addAttribute("digGoods", digGoods);

        //家电
        List<Goods> houseGoods = getCateGoods("家电", userid);
        model.addAttribute("houseGoods", houseGoods);

        //服饰
        List<Goods> colGoods = getCateGoods("服饰", userid);
        model.addAttribute("colGoods", colGoods);

        //书籍
        List<Goods> bookGoods = getCateGoods("书籍", userid);
        model.addAttribute("bookGoods", bookGoods);

        return "main";
    }

    public List<Goods> getCateGoods(String cate, Integer userid) {
        //查询分类
        List<Category> digCategoryList = cateService.selectByName(cate);

        if (digCategoryList.size() == 0) {
            return null;
        }

        //查询属于刚查到的分类的商品
        List<Integer> digCateId = new ArrayList<Integer>();
        for (Category tmp:digCategoryList) {
            digCateId.add(tmp.getCateid());
        }

        List<Goods> goodsList = goodsService.selectByCateLimit(digCateId);

        List<Goods> goodsAndImage = new ArrayList<Goods>();
        //获取每个商品的图片
        for (Goods goods:goodsList) {
            //判断是否为登录状态
            if (userid == null) {
                goods.setFav(false);
            } else {
                Favorite favorite = favoriteService.selectFavByKey(userid, goods.getGoodsid());
                if (favorite == null) {
                    goods.setFav(false);
                } else {
                    goods.setFav(true);
                }
            }

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid());
            goods.setImagePaths(imagePathList);
            goodsAndImage.add(goods);
        }
        return goodsAndImage;
    }
}
