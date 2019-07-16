package com.github.gantleman.shopd.controller.front;

import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.ShopCart;
import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.service.ActivityService;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.OrderService;
import com.github.gantleman.shopd.service.ShopCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ImagePathService imagePathService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/order")
    public String showOrder(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        //查询当前用户的收货地址
        List<Address> addressList = addressService.getAllAddressByUserID(user.getUserid(), request.getServletPath());

        model.addAttribute("address", addressList);

        //订单信息
        //获取当前用户的购物车信息
        List<ShopCart> shopCart = shopCartService.selectByID(user.getUserid());

        //获取购物车中的商品信息
        List<Goods> goodsAndImage = new ArrayList<Goods>();

        Float totalPrice = new Float(0);
        Integer oldTotalPrice = 0;

        for (ShopCart cart:shopCart) {
            Goods goods = goodsService.selectById(cart.getGoodsid());

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid());
            goods.setImagePaths(imagePathList);
            goods.setNum(cart.getGoodsnum());

            //活动信息
            Activity activity = activityService.selectByKey(goods.getActivityid(), request.getServletPath());
            goods.setActivity(activity);

            if(activity.getDiscount() != 1) {
                goods.setNewPrice(goods.getPrice()*goods.getNum()* activity.getDiscount());
            } else if(activity.getFullnum() != null) {
                if (goods.getNum() >= activity.getFullnum()) {
                    goods.setNewPrice((float) (goods.getPrice()*(goods.getNum()-activity.getReducenum())));
                } else {
                    goods.setNewPrice((float) (goods.getPrice()*goods.getNum()));
                }
            } else {
                goods.setNewPrice((float) (goods.getPrice()*goods.getNum()));
            }
            totalPrice = totalPrice + goods.getNewPrice();
            oldTotalPrice = oldTotalPrice + goods.getNum() * goods.getPrice();
            goodsAndImage.add(goods);
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("oldTotalPrice", oldTotalPrice);
        model.addAttribute("goodsAndImage", goodsAndImage);

        return "orderConfirm";
    }

    @RequestMapping("/orderFinish")
    @ResponseBody
    public Msg orderFinish(Float oldPrice, Float newPrice, Boolean isPay, Integer addressid,HttpSession session) {
        User user = (User) session.getAttribute("user");

        //获取订单信息
        List<ShopCart> shopCart = shopCartService.selectByID(user.getUserid());

        //删除购物车
        for (ShopCart cart : shopCart) {
            shopCartService.deleteByKey(cart.getUserid(),cart.getGoodsid());
        }

        //把订单信息写入数据库
        Order order = new Order(null, user.getUserid(), new Date(), oldPrice, newPrice, isPay, false, false, false, addressid,null,null);
        orderService.insertOrder(order);
        
        //插入的订单号
        Integer orderId = order.getOrderid();

        //把订单项写入orderitem表中
        for (ShopCart cart : shopCart) {
            orderItemService.insertOrderItem(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
        }

        return Msg.success("购买成功");
    }

}
