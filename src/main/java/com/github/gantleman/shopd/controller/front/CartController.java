package com.github.gantleman.shopd.controller.front;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.ShopCart;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class CartController {

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ImagePathService imagePathService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/addCart")
    public String addCart(ShopCart shopCart, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }

        //Determine whether you have joined the shopping cart
        ShopCart shopCart1 = shopCartService.selectCartByKey(user.getUserid(), shopCart.getGoodsid());
        if (shopCart1 != null) {
            return "redirect:/showcart";
        }

        //user
        shopCart.setUserid(user.getUserid());

        //data
        shopCart.setCatedate(new Date());

        shopCartService.addShopCart(shopCart);

        //Return to the shopping cart page
        return "redirect:/showcart";
    }

    @RequestMapping("/showcart")
    public String showCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
        return "shopcart";
    }

    @RequestMapping("/cartjson")
    @ResponseBody
    public Msg getCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return Msg.fail("Please login first");
        }

        //Get the current user's shopping cart information
        List<ShopCart> shopCart = shopCartService.selectByID(user.getUserid(), request.getServletPath());

        //Getting information about goods in shopping carts
        List<Goods> goodsAndImage = new ArrayList<Goods>();
        for (ShopCart cart:shopCart) {
            Goods goods = goodsService.selectById(cart.getGoodsid(), request.getServletPath());

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid(),request.getServletPath());
            goods.setImagePaths(imagePathList);
            goods.setNum(cart.getGoodsnum());
            goodsAndImage.add(goods);
        }

        return Msg.success("query was successful").add("shopcart",goodsAndImage);
    }

    @RequestMapping(value = "/deleteCart/{goodsid}", method = RequestMethod.DELETE)
    @ResponseBody
    public Msg deleteCart(@PathVariable("goodsid")Integer goodsid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return Msg.fail("Please login first");
        }

        shopCartService.deleteByKey(user.getUserid(), goodsid);
        return Msg.success("Successful deletion");
    }

    @RequestMapping("/update")
    @ResponseBody
    public Msg updateCart(Integer goodsid,Integer num,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return Msg.fail("Please login first");
        }
        ShopCart shopCart = new ShopCart();
        shopCart.setUserid(user.getUserid());
        shopCart.setGoodsid(goodsid);
        shopCart.setGoodsnum(num);
        shopCartService.updateCartByKey(shopCart);
        return Msg.success("Successful updating of shopping carts");
    }
}
