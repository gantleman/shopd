package com.github.gantleman.shopd.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.gantleman.shopd.entity.Activity;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.entity.Comment;
import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.Goods;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.service.ActivityService;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.CateService;
import com.github.gantleman.shopd.service.CommentService;
import com.github.gantleman.shopd.service.FavoriteService;
import com.github.gantleman.shopd.service.GoodsService;
import com.github.gantleman.shopd.service.ImagePathService;
import com.github.gantleman.shopd.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FrontGoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CateService cateService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ImagePathService imagePathService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public String detailGoods(Integer goodsid, Model model, HttpSession session) {

        if(goodsid == null) {
            return "redirect:/main";
        }

        User user = (User) session.getAttribute("user");

        //The data to be returned exists in HashMap
        Map<String,Object> goodsInfo = new HashMap<String,Object>();

        //Inquire about the basic information of commodities
        Goods goods = goodsService.selectById(goodsid, request.getServletPath());

        if (user == null) {
            goods.setFav(false);
        } else {
            Favorite favorite = favoriteService.selectFavByKey(user.getUserid(), goodsid, request.getServletPath());
            if (favorite == null) {
                goods.setFav(false);
            } else {
                goods.setFav(true);
            }
        }

        //Search for Categories of Goods
        Category category = cateService.selectById(goods.getCategory(), request.getServletPath());

        //Merchandise Pictures
        List<ImagePath> imagePath = imagePathService.findImagePath(goodsid,request.getServletPath());

        //Comments on Commodities

        //Commodity Discount Information
        Activity activity = activityService.selectByKey(goods.getActivityid(), request.getServletPath());
        goods.setActivity(activity);

        //Return data
        goodsInfo.put("goods", goods);
        goodsInfo.put("cate", category);
        goodsInfo.put("image", imagePath);
        model.addAttribute("goodsInfo",goodsInfo);

        //Comment information
        List<Comment> commentList=commentService.selectByGoodsID(goods.getGoodsid(), request.getServletPath());
        for (Integer i=0;i<commentList.size();i++)
        {
            Comment comment=commentList.get(i);
            User commentUser=userService.selectByUserID(comment.getUserid(), request.getServletPath());
            comment.setUserName(commentUser.getUsername());
            commentList.set(i,comment);
        }
        model.addAttribute("commentList",commentList);

        return "detail";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchGoods(@RequestParam(value = "page",defaultValue = "1") Integer pn, String keyword, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());

        //Query data
        List<Goods> goodsList = goodsService.selectByName(keyword, request.getServletPath());

        //Get the picture address
        for (int i = 0; i < goodsList.size(); i++) {
            Goods goods = goodsList.get(i);

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid(),request.getServletPath());

            goods.setImagePaths(imagePathList);

            //Judging whether to collect or not
            if (user == null) {
                goods.setFav(false);
            } else {
                Favorite favorite = favoriteService.selectFavByKey(user.getUserid(), goods.getGoodsid(), request.getServletPath());
                if (favorite == null) {
                    goods.setFav(false);
                } else {
                    goods.setFav(true);
                }
            }

            goodsList.set(i, goods);
        }


        //Display several page numbers
        PageInfo page = new PageInfo(goodsList,5);
        model.addAttribute("pageInfo", page);
        model.addAttribute("keyword", keyword);

        return "search";
    }

    @RequestMapping("/collect")
    @ResponseBody
    public Msg collectGoods(Integer goodsid, HttpSession session) {
        //取登录用户信息,no login重定向至登录页面
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return Msg.fail("Collection failure");
        }

        //添加收藏
        Favorite favorite = new Favorite();
        favorite.setCollecttime(new Date());
        favorite.setGoodsid(goodsid);
        favorite.setUserid(user.getUserid());

        favoriteService.insertFavorite(favorite);

        return Msg.success("Successful collection");
    }

    @RequestMapping("/deleteCollect")
    @ResponseBody
    public Msg deleteFavGoods(Integer goodsid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("Failure to Cancel Collection");
        }

        //删除收藏
        favoriteService.deleteFavByKey(user.getUserid(),goodsid);

        return Msg.success("Successful Cancellation of Collection");
    }

    @RequestMapping("/category")
    public String getCateGoods(String cate, @RequestParam(value = "page",defaultValue = "1") Integer pn, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        //One page shows several data
        PageHelper.startPage(pn, cacheService.PageSize());

        //Query classification ID
        List<Category> categoryList = cateService.selectByNameForRead(cate, request.getServletPath());

        //Get the identified category ID
        List<Integer> cateId = new ArrayList<Integer>();
        for (Category category : categoryList) {
            cateId.add(category.getCateid());
        }

        //Query data
        List<Goods> goodsList = goodsService.selectByDetailcateAndID(cateId, request.getServletPath());

        //Get the picture address
        for (int i = 0; i < goodsList.size(); i++) {
            Goods goods = goodsList.get(i);

            List<ImagePath> imagePathList = imagePathService.findImagePath(goods.getGoodsid(),request.getServletPath());

            goods.setImagePaths(imagePathList);

            //Judging whether to collect or not
            if (user == null) {
                goods.setFav(false);
            } else {
                Favorite favorite = favoriteService.selectFavByKey(user.getUserid(), goods.getGoodsid(), request.getServletPath());
                if (favorite == null) {
                    goods.setFav(false);
                } else {
                    goods.setFav(true);
                }
            }

            goodsList.set(i, goods);
        }


        //Display several page numbers
        PageInfo page = new PageInfo(goodsList,5);
        model.addAttribute("pageInfo", page);
        model.addAttribute("cate", cate);
        return "category";
    }



    @RequestMapping("/comment")
    @ResponseBody
    public Msg comment(Comment comment, HttpServletRequest request){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("Comment Failure");
        }
        comment.setUserid(user.getUserid());
        Date date=new Date();
        comment.setCommenttime(date);
        commentService.insertSelective(comment);
        return Msg.success("comment Successful");
    }
}
