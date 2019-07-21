package com.github.gantleman.shopd.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.gantleman.shopd.entity.Address;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.AddressService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.OrderService;
import com.github.gantleman.shopd.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ImagePathService ImagePathService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/login")
    public String loginView(){
        return "login";
    }

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @RequestMapping("/registerresult")
    public String registerResult(User user,Model registerResult){
        List<User> userList=new ArrayList<User>();
        userList=userService.selectByName(user.getUsername());
        if (!userList.isEmpty())
        {
            registerResult.addAttribute("errorMsg","用户名被占用");
            return "register";
        }
        else {
            Date RegTime=new Date();
            user.setRegtime(RegTime);
            userService.insertSelective(user);
            return  "redirect:/login";
        }
    }


    @RequestMapping("/loginconfirm")
    public String loginConfirm(User user,Model loginResult,HttpServletRequest request,@RequestParam("confirmlogo") String confirmlogo){
        HttpSession session=request.getSession();
        String verificationCode = (String) session.getAttribute("certCode");
       /* if (!confirmlogo.equals(verificationCode))
        {
            loginResult.addAttribute("errorMsg","验证码错误");
            return "login";

        }*/
        List<User> userList=new ArrayList<User>();
        userList=userService.selectByNameAndPasswrod(user.getUsername(), user.getPassword());
        if (!userList.isEmpty())
        {
            session.setAttribute("user",userList.get(0));
            return "redirect:/main";
        }
        else {
            loginResult.addAttribute("errorMsg","用户名与密码不匹配");
            return "login";
        }
    }

    @RequestMapping("/information")
    public String information(Model userModel,HttpServletRequest request){
        HttpSession session=request.getSession();
        User user;
        Integer userId;
        user=(User) session.getAttribute("user");
        if (user==null)
        {
            return "redirect:/login";
        }
        userId=user.getUserid();
        user=userService.selectByUserID(userId, request.getServletPath());
        userModel.addAttribute("user",user);
        return "information";
    }

    @RequestMapping("/saveInfo")
    @ResponseBody
    public Msg saveInfo(String name, String email, String telephone,HttpServletRequest request){
        HttpSession session=request.getSession();
        User user,updateUser=new User();
        List<User> userList=new ArrayList<User>();
        Integer userid;
        user=(User)session.getAttribute("user");
        userid= user.getUserid();
        userList=userService.selectByName(name);
        if (userList.isEmpty())
        {
            updateUser.setUserid(userid);
            updateUser.setUsername(name);
            updateUser.setEmail(email);
            updateUser.setTelephone(telephone);
            userService.updateByPrimaryKeySelective(updateUser);
            return Msg.success("successful");
        }
        else  {return Msg.fail("更新失败");}
    }

    @Autowired
    private AddressService addressService;

    @RequestMapping("/info/address")
    public String address(HttpServletRequest request,Model addressModel){
        HttpSession session=request.getSession();
        User user=(User)session.getAttribute("user");
        if (user==null)
        {
            return "redirect:/login";
        }
        List<Address> addressList=addressService.getAllAddressByUser(user.getUserid(),request.getServletPath());
        addressModel.addAttribute("addressList",addressList);
        return "address";
    }

    @RequestMapping("/saveAddr")
    @ResponseBody
    public Msg saveAddr(Address address){

        addressService.updateByPrimaryKeySelective(address);
        return Msg.success("修改成功");
    }

    @RequestMapping("/deleteAddr")
    @ResponseBody
    public Msg deleteAddr(Address address){
        addressService.deleteByPrimaryKey(address.getAddressid());
        return Msg.success("删除成功");
    }

    @RequestMapping("/insertAddr")
    @ResponseBody
    public Msg insertAddr(Address address,HttpServletRequest request){
       HttpSession session=request.getSession();
       User user=new User();
       user=(User) session.getAttribute("user");
       address.setUserid(user.getUserid());
        addressService.insertSelective(address);
        return Msg.success("添加成功");
    }

    @RequestMapping("/info/list")
    public String list(HttpServletRequest request,Model orderModel){

        HttpSession session=request.getSession();
        User user;
        user=(User)session.getAttribute("user");

        if (user==null)
        {
            return "redirect:/login";
        }

        List<Order> orderList=orderService.selectOrderByIUserID(user.getUserid(), request.getServletPath());
        orderModel.addAttribute("orderList",orderList);
        Order order;
        OrderItem orderItem;
        List<OrderItem> orderItemList=new ArrayList<OrderItem>();
        Address address;
       for (Integer i=0;i<orderList.size();i++)
       {
           order=orderList.get(i);
           orderItemList=orderItemService.getOrderItemByOrderId(order.getOrderid(), request.getServletPath());
           List<Goods> goodsList=new ArrayList<Goods>();
           List<Integer> goodsIdList=new ArrayList<Integer>();
           for (Integer j=0;j<orderItemList.size();j++)
           {
               orderItem=orderItemList.get(j);
               goodsIdList.add(orderItem.getGoodsid());
           }
           goodsList=goodsService.selectByID(goodsIdList, request.getServletPath());

           order.setGoodsInfo(goodsList);
           address=addressService.getAddressByKey(order.getAddressid(), request.getServletPath());
           order.setAddress(address);
           orderList.set(i,order);
       }

       orderModel.addAttribute("orderList",orderList);

        return "list";
    }

   

    @RequestMapping("/deleteList")
    @ResponseBody
    public Msg deleteList(Order order){
        orderService.deleteById(order.getOrderid());
        return Msg.success("删除成功");
    }


    @RequestMapping("/info/favorite")
    public String showFavorite(@RequestParam(value = "page",defaultValue = "1") Integer pn, HttpServletRequest request,Model model){
        HttpSession session=request.getSession();
        User user=(User)session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());
        List<Favorite> favoriteList = favoriteService.selectFavByUser(user.getUserid(), request.getServletPath());

        List<Integer> goodsIdList = new ArrayList<Integer>();
        for (Favorite tmp:favoriteList) {
            goodsIdList.add(tmp.getGoodsid());
        }
        
        List<Goods> goodsList = new ArrayList<Goods>();
        if (!goodsIdList.isEmpty()) {
            goodsList = goodsService.selectByID(goodsIdList, request.getServletPath());
        }

        //获取图片地址
        for (int i = 0; i < goodsList.size(); i++) {
            Goods goods = goodsList.get(i);

            List<ImagePath> imagePathList = ImagePathService.findImagePath(goods.getGoodsid(),request.getServletPath());

            goods.setImagePaths(imagePathList);

            //判断是否收藏
            goods.setFav(true);

            goodsList.set(i, goods);
        }

        //Display several page numbers
        PageInfo page = new PageInfo(goodsList,5);
        model.addAttribute("pageInfo", page);

        return "favorite";
    }

    @RequestMapping("/savePsw")
    @ResponseBody
    public Msg savePsw(String Psw,HttpServletRequest request)
    {
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        user.setPassword(Psw);
        userService.updateByPrimaryKeySelective(user);
        return Msg.success("修改密码成功");
    }

    @RequestMapping("/finishList")
    @ResponseBody
    public Msg finishiList(Integer orderid){
        Order order=new Order();
        order.setOrderid(orderid);
        order.setIsreceive(true);
        order.setIscomplete(true);
        orderService.updateOrderByKey(order);
        return Msg.success("完成订单成功");
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session=request.getSession();
        session.removeAttribute("user");
        return "redirect:/login";
    }

}
