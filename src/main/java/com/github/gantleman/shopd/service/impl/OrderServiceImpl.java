package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.OrderDA;
import com.github.gantleman.shopd.da.OrderUserDA;
import com.github.gantleman.shopd.da.OrderisreceiveDA;
import com.github.gantleman.shopd.da.OrderissendDA;
import com.github.gantleman.shopd.dao.OrderMapper;
import com.github.gantleman.shopd.entity.Order;
import com.github.gantleman.shopd.entity.OrderExample;
import com.github.gantleman.shopd.entity.OrderUser;
import com.github.gantleman.shopd.entity.Orderisreceive;
import com.github.gantleman.shopd.entity.Orderissend;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.OrderService;
import com.github.gantleman.shopd.service.jobs.OrderJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private OrderJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "Order";

    private String classname_extra = "Order_User";

    private String classname_Issend = "Order_Issend";

    private String classname_Isreceive = "Order_Isreceive";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, OrderJob.class, null, job);
        }
    }   

    public List<Order> selectorderby(String tablename, String url) {
        List<Order> re = new ArrayList<Order>();

        if(redisu.hasKey(tablename)){
            Set<Object> so = redisu.sGet(tablename);
            for(Object i: so){
                Integer ii = (Integer) i;
                Order order = getOrderByKey(ii, url);

                if(order != null){
                    re.add(order);
                }
            }
        }else{
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderispage", 0);
            }else{
                RefreshIsDBD(true);
            }

            if(redisu.hasKey(tablename)){
                Set<Object> so = redisu.sGet(tablename);
                for(Object i: so){
                    Integer ii = (Integer) i;
                    Order order = getOrderByKey(ii, url);
    
                    if(order != null){
                        re.add(order);
                    }
                }
            }
        }

        return re;
    }

    @Override
    public List<Order> selectOrderByIssend(String url) {
        return selectorderby(classname_Issend, url);
    }

    @Override
    public List<Order> selectOrderByIssendAndIsreceive(String url) {
        return selectorderby(classname_Isreceive, url);
    }
    
    @Override
    public List<Order> selectOrderByAll(Integer pageId,  String url) {
        List<Order> re = new ArrayList<Order>();

        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Order r = (Order) redisu.hget(classname, pageId.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            Integer i = cacheService.PageBegin(pageId);
            Integer l = cacheService.PageEnd(pageId);
            for(;i < l; i++){
                Order r = (Order) redisu.hget(classname, i.toString());
                if(r != null)
                    re.add(r);
            }

            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }
        return re; 
    }

    public List<Order> selectOrderByIUserID(Integer userID, String url) {
        List<Order> re = new ArrayList<Order>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("order_u"+userID.toString());
            if(ro != null) {
                for (Object id : ro) {
                    Order r =  getOrderByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);                
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderuserpage", userID);
            }else{
                RefreshUserDBD(userID, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("order_u"+userID.toString());
                if(ro != null) {
                    for (Object id : ro) {
                        Order r =  getOrderByKey((Integer)id, url);
                        if (r != null)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);                
                }
            }
        }
        return re;
    }

    @Override
    public Order getOrderByKey(Integer orderid, String url) {
        Order re = null;
        Integer pageId = cacheService.PageID(orderid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (Order) redisu.hget(classname, orderid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderpage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname, orderid.toString())) {
                //read redis
                re = (Order) redisu.hget(classname, orderid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    public void insertSelective_extra(Order order) {
        //add to OrderUserDA
        RefreshUserDBD(order.getUserid(), false, false);
        BDBEnvironmentManager.getInstance();
        OrderUserDA orderUserDA=new OrderUserDA(BDBEnvironmentManager.getMyEntityStore());
        OrderUser orderUser = orderUserDA.findOrderUserById(order.getUserid());
        if(orderUser == null){
            orderUser = new OrderUser();
        }
        orderUser.addOrderList(order.getOrderid());
        orderUserDA.saveOrderUser(orderUser);

        //Re-publish to redis
        redisu.sAdd("order_u" + order.getUserid().toString(), order.getOrderid()); 
    }


    @Override
    public void insertOrder(Order order) {
        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        order.setOrderid(new Long(id).intValue());
        order.setStatus(CacheService.STATUS_INSERT);
        orderDA.saveOrder(order);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, order.getOrderid().toString(), (Object)order, 0);

        insertSelective_extra(order);
    }

    public void deleteByPrimaryKey_extra(Order order) {
        RefreshUserDBD(order.getUserid(), false, false);

        BDBEnvironmentManager.getInstance();
        OrderUserDA orderUserDA=new OrderUserDA(BDBEnvironmentManager.getMyEntityStore());
        OrderUser orderUser = orderUserDA.findOrderUserById(order.getUserid());

        if(orderUser != null){
            orderUser.removeOrderList(order.getOrderid());
            if(orderUser.getOrderSize() >= 1){
                //Re-publish to redis
                 redisu.setRemove("order_u" + order.getUserid().toString(), order.getOrderid());
            } else if(orderUser.getOrderSize() == 0){
                //list empty
                orderUserDA.removedOrderUserById(orderUser.getUserid());
                //Re-publish to redis
                redisu.del("order_u" + order.getUserid().toString());
            }
            orderUserDA.saveOrderUser(orderUser);
            BDBEnvironmentManager.getMyEntityStore().sync();
        }
    }

    @Override
    public void deleteById(Integer orderid) {
        RefreshIsDBD(false);
        RefreshDBD(cacheService.PageID(orderid), false);

        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        Order order = orderDA.findOrderById(orderid);
 
        if (order != null)
        {
             order.setStatus(CacheService.STATUS_DELETE);
             orderDA.saveOrder(order);

             //Re-publish to redis
             redisu.hdel(classname, order.getOrderid().toString(), 0);
     
             ChangeToDel(order.getOrderid());

             deleteByPrimaryKey_extra(order);
        }   
    }

    @Override
    public void updateOrderByKey(Order iorder) {
        RefreshIsDBD(false);
        RefreshDBD(cacheService.PageID(iorder.getOrderid()), false);
        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        Order order = orderDA.findOrderById(iorder.getOrderid());
 
        if (order != null)
        {
            if(iorder.getStatus()== null)
                iorder.setStatus(CacheService.STATUS_UPDATE);
            
            if(iorder.getIspay() != null){
                order.setIspay(iorder.getIspay());
            }
            
            if(iorder.getIssend() != null){
                order.setIssend(iorder.getIssend());

                if(order.getIssend() == false && iorder.getIssend()){
                    ChangeToSend(order.getOrderid());
                }
            }
            if(iorder.getIsreceive() != null){
                order.setIsreceive(iorder.getIsreceive());

                if(order.getIsreceive() == false && iorder.getIsreceive()){
                    ChangeToReceive(order.getOrderid());
                }
            }
            if(iorder.getIscomplete() != null){
                order.setIscomplete(iorder.getIscomplete());

                if(order.getIscomplete() == false && iorder.getIscomplete()){
                    ChangeToComplete(order.getOrderid());
                }
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
                redisu.sAdd("Order_u"+order.getUserid().toString(),(Object)order.getOrderid());
            } 
            orderDA.saveOrder(order);

            //Re-publish to redis
            redisu.sAdd("Order_u"+order.getUserid().toString(), (Object)order.getOrderid());
            redisu.hset(classname, order.getOrderid().toString(), (Object)order, 0);
        }
    }

    @Override
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        OrderUserDA orderUserDA=new OrderUserDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                OrderUser orderUser = orderUserDA.findOrderUserById(i);
                if(orderUser != null){
                    orderUserDA.removedOrderUserById(orderUser.getUserid());
                    redisu.del("order_u"+orderUser.getUserid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (orderUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                Order order = orderDA.findOrderById(i);
                if(order != null){
                    if(null ==  order.getStatus()) {
                        orderDA.removedOrderById(order.getOrderid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  order.getStatus() && 1 == orderMapper.deleteByPrimaryKey(order.getOrderid())) {
                        orderDA.removedOrderById(order.getOrderid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  order.getStatus()  && 1 == orderMapper.insert(order)) {
                        orderDA.removedOrderById(order.getOrderid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  order.getStatus() && 1 == orderMapper.updateByPrimaryKey(order)) {
                        orderDA.removedOrderById(order.getOrderid());
                    }
                    redisu.hdel(classname, order.getOrderid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (orderDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, OrderJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<Order> re = new ArrayList<Order>();          
            OrderExample orderExample = new OrderExample();
            orderExample.or().andOrderidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andOrderidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = orderMapper.selectByExample(orderExample);
            for (Order value : re) {
                redisu.hset(classname, value.getOrderid().toString(), value);
                orderDA.saveOrder(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                OrderDA orderDA=new OrderDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    Order r = orderDA.findOrderById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer userID, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        OrderUserDA orderUserDA=new OrderUserDA(BDBEnvironmentManager.getMyEntityStore());
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(userID))) {
            /// init
            List<Order> re = new ArrayList<Order>();          
            OrderExample orderExample = new OrderExample();
            orderExample.or().andUseridGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(userID)))
            .andUseridLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(userID)));

            re = orderMapper.selectByExample(orderExample);
            for (Order value : re) {
                OrderUser orderUser  = orderUserDA.findOrderUserById(value.getUserid());
                if(orderUser == null){
                    orderUser = new OrderUser();
                }
                
                redisu.sAdd("order_u"+value.getUserid().toString(), (Object)value.getOrderid());

                if(andAll){ 
                    RefreshDBD(cacheService.PageID(value.getOrderid()), refresRedis);
                }

                orderUserDA.saveOrderUser(orderUser);               
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(userID).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(userID));
                Integer l = cacheService.PageEnd(cacheService.PageID(userID));
                for(;i < l; i++){
                    OrderUser r = orderUserDA.findOrderUserById(i);
                    if(r!= null){
                        List<Integer> li = r.getOrderList();
                        for(Integer id: li){
                            redisu.sAdd("order_u"+r.getUserid().toString(), (Object)id);
                        }
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(userID).toString(), 1);
            }
        }
    }

    @Override
    public void RefreshIsDBD(boolean refresRedis) {
        if(!cacheService.IsCache(classname_Issend)){
            BDBEnvironmentManager.getInstance();
            OrderissendDA orderissendDA=new OrderissendDA(BDBEnvironmentManager.getMyEntityStore());
           
            OrderExample orderExample = new OrderExample();
            orderExample.or().andIspayEqualTo(true)
            .andIssendEqualTo(false);

            List<Order> re = orderMapper.selectByExample(orderExample);

            for(Order o: re){
                Orderissend orderissend = new Orderissend();
                orderissend.setOrderid(o.getOrderid());
                orderissendDA.saveOrderissend(orderissend);

                redisu.sAdd(classname_Issend, o.getOrderid());
            }

            cacheService.EventCteateLocalCache(classname_Issend);
        } if (refresRedis){
            BDBEnvironmentManager.getInstance();
            OrderissendDA orderissendDA=new OrderissendDA(BDBEnvironmentManager.getMyEntityStore());
            if(orderissendDA != null){
                List<Orderissend> re = orderissendDA.findAllOrderissend();

                for(Orderissend o: re){    
                    redisu.sAdd(classname_Issend, o.getOrderid());
                }
            }          
        }

        if(!cacheService.IsCache(classname_Isreceive)){
            BDBEnvironmentManager.getInstance();
            OrderisreceiveDA orderisreceiveDA=new OrderisreceiveDA(BDBEnvironmentManager.getMyEntityStore());
           
            OrderExample orderExample = new OrderExample();
            orderExample.or().andIssendEqualTo(true)
            .andIsreceiveEqualTo(false);

            List<Order> re = orderMapper.selectByExample(orderExample);

            for(Order o: re){
                Orderisreceive orderisreceive = new Orderisreceive();
                orderisreceive.setOrderid(o.getOrderid());
                orderisreceiveDA.saveOrderisreceive(orderisreceive);

                redisu.sAdd(classname_Isreceive, o.getOrderid());
            }
            
            cacheService.EventCteateLocalCache(classname_Isreceive);
        }if (refresRedis){
            BDBEnvironmentManager.getInstance();
            OrderisreceiveDA orderisreceiveDA=new OrderisreceiveDA(BDBEnvironmentManager.getMyEntityStore());
            if(orderisreceiveDA != null){
                List<Orderisreceive> re = orderisreceiveDA.findAllOrderisreceive();

                for(Orderisreceive o: re){    
                    redisu.sAdd(classname_Issend, o.getOrderid());
                }
            }             
        }
    }

    public void ChangeToSend(Integer orderid) {
        BDBEnvironmentManager.getInstance();
        OrderissendDA orderissendDA=new OrderissendDA(BDBEnvironmentManager.getMyEntityStore());
        Orderissend orderissend = new Orderissend();
        orderissend.setOrderid(orderid);
        orderissendDA.saveOrderissend(orderissend);
        redisu.sAdd(classname_Issend, orderid);
    }

    public void ChangeToReceive(Integer orderid) {
        BDBEnvironmentManager.getInstance();
        OrderissendDA orderissendDA=new OrderissendDA(BDBEnvironmentManager.getMyEntityStore());
        orderissendDA.removedOrderissendById(orderid);
        redisu.setRemove(classname_Issend, orderid);

        BDBEnvironmentManager.getInstance();
        OrderisreceiveDA orderisreceiveDA=new OrderisreceiveDA(BDBEnvironmentManager.getMyEntityStore());
        Orderisreceive orderisreceive = new Orderisreceive();
        orderisreceive.setOrderid(orderid);
        orderisreceiveDA.saveOrderisreceive(orderisreceive);
        redisu.sAdd(classname_Isreceive, orderid);
    }

    public void ChangeToComplete(Integer orderid) {
        BDBEnvironmentManager.getInstance();
        OrderisreceiveDA orderisreceiveDA=new OrderisreceiveDA(BDBEnvironmentManager.getMyEntityStore());
        orderisreceiveDA.removedOrderisreceiveById(orderid);
        redisu.setRemove(classname_Isreceive, orderid);
    }

    public void ChangeToDel(Integer orderid) {
        BDBEnvironmentManager.getInstance();
        OrderissendDA orderissendDA=new OrderissendDA(BDBEnvironmentManager.getMyEntityStore());
        orderissendDA.removedOrderissendById(orderid);
        redisu.setRemove(classname_Issend, orderid);

        BDBEnvironmentManager.getInstance();
        OrderisreceiveDA orderisreceiveDA=new OrderisreceiveDA(BDBEnvironmentManager.getMyEntityStore());
        orderisreceiveDA.removedOrderisreceiveById(orderid);
        redisu.setRemove(classname_Isreceive, orderid);
    }
}
