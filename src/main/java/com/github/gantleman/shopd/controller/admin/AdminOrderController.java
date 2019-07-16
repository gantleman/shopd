package com.github.gantleman.shopd.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/send")
    public String sendOrder(@RequestParam(value = "page",defaultValue = "1")Integer pn, Model model, HttpSession session) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        //一页显示几个数据
        PageHelper.startPage(pn, 2);

        //查询未发货订单
        List<Order> orderList = orderService.selectOrderByIssend();
        model.addAttribute("sendOrder", orderList);

        //查询该订单中的商品
        for (int i = 0; i < orderList.size(); i++) {
            //获取订单项中的goodsid
            Order order = orderList.get(i);
            List<OrderItem> orderItemList = orderItemService.getOrderItemByOrderId(order.getOrderid());
            List<Goods> goodsList = new ArrayList<Goods>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }

            //根据goodsid查询商品
            order.setGoodsInfo(goodsList);

            //查询地址
            Address address = addressService.getAddressByKey(order.getAddressid(),request.getServletPath());
            order.setAddress(address);

            orderList.set(i, order);
        }

        //显示几个页号
        PageInfo page = new PageInfo(orderList,5);
        model.addAttribute("pageInfo", page);

        return "adminAllOrder";
    }

    @RequestMapping("/sendGoods")
    public String sendGoods(Integer orderid, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        Order order = new Order();
        order.setOrderid(orderid);
        order.setIssend(true);
        orderService.updateOrderByKey(order);
        return "redirect:/admin/order/send";
    }

    @RequestMapping("/receiver")
    public String receiveOrder(@RequestParam(value = "page",defaultValue = "1")Integer pn, Model model,HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        //一页显示几个数据
        PageHelper.startPage(pn, 2);

        //查询未收货订单
        List<Order> orderList = orderService.selectOrderByIssendAndIsreceive();
        model.addAttribute("sendOrder", orderList);

        //查询该订单中的商品
        for (int i = 0; i < orderList.size(); i++) {
            //获取订单项中的goodsid
            Order order = orderList.get(i);
            List<OrderItem> orderItemList = orderItemService.getOrderItemByOrderId(order.getOrderid());
            List<Goods> goodsList = new ArrayList<Goods>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }
            //根据goodsid查询商品
            order.setGoodsInfo(goodsList);

            //查询地址
            Address address = addressService.getAddressByKey(order.getAddressid(), request.getServletPath());
            order.setAddress(address);

            orderList.set(i, order);
        }

        //显示几个页号
        PageInfo page = new PageInfo(orderList,5);
        model.addAttribute("pageInfo", page);

        return "adminOrderReceive";
    }

    @RequestMapping("/complete")
    public String completeOrder(@RequestParam(value = "page", defaultValue = "1") Integer pn, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        //一页显示几个数据
        PageHelper.startPage(pn, 2);

        //查询已完成订单
        List<Order> orderList = orderService.selectOrderByIssendAndIsreceiveAndIscomplete();
        model.addAttribute("sendOrder", orderList);

        //查询该订单中的商品
        for (int i = 0; i < orderList.size(); i++) {
            //获取订单项中的goodsid
            Order order = orderList.get(i);
            List<OrderItem> orderItemList = orderItemService.getOrderItemByOrderId(order.getOrderid());
            List<Goods> goodsList = new ArrayList<Goods>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }

            //根据goodsid查询商品
            order.setGoodsInfo(goodsList);

            //查询地址
            Address address = addressService.getAddressByKey(order.getAddressid(), request.getServletPath());
            order.setAddress(address);

            orderList.set(i, order);
        }

        //显示几个页号
        PageInfo page = new PageInfo(orderList, 5);
        model.addAttribute("pageInfo", page);
        return "adminOrderComplete";
    }
}
