package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.OrderDA;
import com.github.gantleman.shopd.dao.OrderMapper;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.OrderService;
import com.github.gantleman.shopd.service.jobs.OrderJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private OrderJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Order";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, OrderJob.class, null, job);
        }
    }   

    @Override
    public List<Order> selectOrderByIssend() {
        List<Order> re = new ArrayList();
        if(redisu.hasKey(classname)){
            //write redis          
            re = orderMapper.selectByExample(new OrderExample());

            ///read and write
            for( Order item : re ){
                redisu.sAddAndTime("Order_u"+item.getUserid().toString(), 0, (Object)item.getOrderid());
                redisu.hset(classname, item.getOrderid().toString(), item);

                if(item.getIssend()&&!item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_issend", 0, item.getOrderid());
                }else if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_isreceive", 0, item.getOrderid());
                }else  if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_iscomplete", 0, item.getOrderid());
                }
            }
            redisu.expire(classname, 0);                 
        }

        Set<Object> ro = redisu.sGet("Order_issend");
        for(Object rv: ro){
            Integer orderid = (Integer)rv;
            Object rh = redisu.hget(classname, orderid.toString());
            if(rh != null)
                re.add((Order)rh);
        }

        return re;
    }

    @Override
    public List<Order> selectOrderByIssendAndIsreceive() {
        List<Order> re = new ArrayList();
        if(redisu.hasKey(classname)){
            //write redis          
            re = orderMapper.selectByExample(new OrderExample());

            ///read and write
            for( Order item : re ){
                redisu.sAddAndTime("Order_u"+item.getUserid().toString(), 0, (Object)item.getOrderid());
                redisu.hset(classname, item.getOrderid().toString(), item);

                if(item.getIssend()&&!item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_issend", 0, item.getOrderid());
                }else if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_isreceive", 0, item.getOrderid());
                }else  if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_iscomplete", 0, item.getOrderid());
                }
            }
            redisu.expire(classname, 0);                 
        }

        Set<Object> ro = redisu.sGet("Order_isreceive");
        for(Object rv: ro){
            Integer orderid = (Integer)rv;
            Object rh = redisu.hget(classname, orderid.toString());
            if(rh != null)
                re.add((Order)rh);
        }

        return re;
    }
    
    @Override
    public List<Order> selectOrderByIssendAndIsreceiveAndIscomplete() {
        List<Order> re = new ArrayList();
        if(redisu.hasKey(classname)){
            //write redis          
            re = orderMapper.selectByExample(new OrderExample());

            ///read and write
            for( Order item : re ){
                redisu.sAddAndTime("Order_u"+item.getUserid().toString(), 0, (Object)item.getOrderid());
                redisu.hset(classname, item.getOrderid().toString(), item);

                if(item.getIssend()&&!item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_issend", 0, item.getOrderid());
                }else if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_isreceive", 0, item.getOrderid());
                }else  if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                    redisu.sAddAndTime("Order_iscomplete", 0, item.getOrderid());
                }
            }
            redisu.expire(classname, 0);                 
        }

        Set<Object> ro = redisu.sGet("Order_iscomplete");
        for(Object rv: ro){
            Integer orderid = (Integer)rv;
            Object rh = redisu.hget(classname, orderid.toString());
            if(rh != null)
                re.add((Order)rh);
        }

        return re;
    }

    public List<Order> selectOrderByIUserID(Integer UserID) {

        List<Order> re = null;
        if(redisu.hasKey("Order_u"+UserID.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("Order_u"+UserID.toString());
            re = new ArrayList<Order>();
            for (Object id : ro) {
                Order r =  (Order) redisu.hget(classname, ((Integer)id).toString());
                if (r != null)
                    re.add(r);
            }
            redisu.expire("Order_u"+UserID.toString(), 0);
            redisu.expire(classname, 0);
        }else {

            if(redisu.hasKey(classname)){
                //write redis          
                re = orderMapper.selectByExample(new OrderExample());

                ///read and write
                for( Order item : re ){
                    redisu.sAddAndTime("Order_u"+item.getUserid().toString(), 0, (Object)item.getOrderid());
                    redisu.hset(classname, item.getOrderid().toString(), item);

                    if(item.getIssend()&&!item.getIsreceive()&&!item.getIscomplete()){
                        redisu.sAddAndTime("Order_issend", 0, item.getOrderid());
                    }else if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                        redisu.sAddAndTime("Order_isreceive", 0, item.getOrderid());
                    }else  if(item.getIssend()&&item.getIsreceive()&&!item.getIscomplete()){
                        redisu.sAddAndTime("Order_iscomplete", 0, item.getOrderid());
                    }
                }
                redisu.expire(classname, 0);        
            }
        }
        return re;
    }

    @Override
    public void insertOrder(Order order) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        order.setOrderid(new Long(id).intValue());
        order.MakeStamp();
        order.setStatus(2);
        orderDA.saveOrder(order);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.sAddAndTime("Order_u"+order.getUserid().toString(), 0, (Object)order.getOrderid());
        if(order.getIssend()&&!order.getIsreceive()&&!order.getIscomplete()){
            redisu.sAddAndTime("Order_issend", 0, order.getOrderid());
        }else if(order.getIssend()&&order.getIsreceive()&&!order.getIscomplete()){
            redisu.sAddAndTime("Order_isreceive", 0, order.getOrderid());
        }else  if(order.getIssend()&&order.getIsreceive()&&!order.getIscomplete()){
            redisu.sAddAndTime("Order_iscomplete", 0, order.getOrderid());
        }
        redisu.hset(classname, order.getOrderid().toString(), order, 0);
    }

    @Override
    public void deleteById(Integer orderid) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        Order order = orderDA.findOrderById(orderid);
 
        if (order != null)
        {
             order.MakeStamp();
             order.setStatus(1);
             orderDA.saveOrder(order);
 
             //Re-publish to redis
             redisu.setRemove("Order_u"+order.getUserid().toString());
             if(order.getIssend()&&!order.getIsreceive()&&!order.getIscomplete()){
                 redisu.setRemove("Order_issend", order.getOrderid());
             }else if(order.getIssend()&&order.getIsreceive()&&!order.getIscomplete()){
                 redisu.setRemove("Order_isreceive", order.getOrderid());
             }else  if(order.getIssend()&&order.getIsreceive()&&!order.getIscomplete()){
                 redisu.setRemove("Order_iscomplete", order.getOrderid());
             }
             redisu.hdel(classname, order.getOrderid().toString());
        } 
    }

    @Override
    public void updateOrderByKey(Order iorder) {

        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        Order order = orderDA.findOrderById(iorder.getOrderid());
 
        if (order != null)
        {
            iorder.MakeStamp();
            iorder.setStatus(3);
            
            if(iorder.getIscomplete() != null){
                order.setIscomplete(iorder.getIscomplete());
                redisu.setRemove("Order_isreceive", order.getOrderid());
                redisu.sAddAndTime("Order_iscomplete", 0, order.getOrderid());
            }
            if(iorder.getIspay() != null){
                order.setIspay(iorder.getIspay());
            }
            if(iorder.getIsreceive() != null){
                order.setIsreceive(iorder.getIsreceive());
                redisu.setRemove("Order_issend", order.getOrderid());
                redisu.sAddAndTime("Order_isreceive", 0, order.getOrderid());
            }
            if(iorder.getIssend() != null){
                order.setIssend(iorder.getIssend());
                redisu.sAddAndTime("Order_issend", 0, order.getOrderid());
            }
            if(iorder.getAddress() != null){
                order.setAddress(iorder.getAddress());
            }
            if(iorder.getAddressid() != null){
                order.setAddressid(iorder.getAddressid());
            }
            if(iorder.getGoodsInfo() != null){
                order.setGoodsInfo(iorder.getGoodsInfo());
            }
            if(iorder.getNewprice() != null){
                order.setNewprice(iorder.getNewprice());
            }
            if(iorder.getOldprice() != null){
                order.setOldprice(iorder.getOldprice());
            }
            if(iorder.getOrdertime() != null){
                order.setOrdertime(iorder.getOrdertime());
            }
            if(iorder.getUserid() != null){
                redisu.setRemove("Order_u"+order.getUserid().toString(), (Object)order.getOrderid());
                order.setUserid(iorder.getUserid());
                redisu.sAddAndTime("Order_u"+order.getUserid().toString(), 0, (Object)order.getOrderid());
            } 
            orderDA.saveOrder(order);

            //Re-publish to redis
            redisu.sAddAndTime("Order_u"+order.getUserid().toString(), 0, (Object)order.getOrderid());
            redisu.hset(classname, order.getOrderid().toString(), (Object)order, 0);
        }
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        List<Order> lorder = orderDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (Order order : lorder) {
            if(null ==  order.getStatus()&& order.getIscomplete()) {
                orderDA.removedOrderById(order.getOrderid());
            }

            if(1 ==  order.getStatus() && 1 == orderMapper.deleteByPrimaryKey(order.getOrderid())) {
                orderDA.removedOrderById(order.getOrderid());
            }

            if(2 ==  order.getStatus()  && 1 == orderMapper.insert(order)) {
                orderDA.removedOrderById(order.getOrderid());
            }

            if(3 ==  order.getStatus() && 1 == orderMapper.updateByPrimaryKey(order)) {
                orderDA.removedOrderById(order.getOrderid());
            }
        }

        if (orderDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        ///init
       if (cacheService.IsCache(classname)) {
           BDBEnvironmentManager.getInstance();
           OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<Order> re = new ArrayList<Order>();

           OrderExample orderExample = new OrderExample();
           re = orderMapper.selectByExample(orderExample);
           for (Order value : re) {
               value.MakeStamp();
               orderDA.saveOrder(value);

               redisu.sAddAndTime("Order_u"+ value.getUserid(), 0, value.getOrderid());
               redisu.hset(classname, value.getOrderid().toString(), value, 0);

                if(value.getIssend()&&!value.getIsreceive()&&!value.getIscomplete()){
                    redisu.sAddAndTime("Order_issend", 0, value.getOrderid());
                }else if(value.getIssend()&&value.getIsreceive()&&!value.getIscomplete()){
                    redisu.sAddAndTime("Order_isreceive", 0, value.getOrderid());
                }else  if(value.getIssend()&&value.getIsreceive()&&!value.getIscomplete()){
                    redisu.sAddAndTime("Order_iscomplete", 0, value.getOrderid());
                }
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, OrderJob.class, null, job);          
           }
       }
   }
}
