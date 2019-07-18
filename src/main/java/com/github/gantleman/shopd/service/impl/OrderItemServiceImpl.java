package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.OrderItemDA;
import com.github.gantleman.shopd.da.OrderitemOrderDA;
import com.github.gantleman.shopd.dao.OrderItemMapper;
import com.github.gantleman.shopd.dao.OrderitemOrderMapper;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.entity.OrderitemOrder;
import com.github.gantleman.shopd.entity.OrderitemOrderExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.jobs.OrderItemJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service("orderitemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired(required = false)
    private  OrderitemOrderMapper orderitemOrderMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisUtil redisu;

    @Autowired
    private OrderItemJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "OrderItem";

    private String classname_extra = "OrderItem_User";

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, OrderItemJob.class, null, job);
        }
    } 

    @Override
    public List<OrderItem> getOrderItemByOrderId(Integer orderid, String url) {
        List<OrderItem> re = null;

        if(redisu.hasKey("orderitem_u"+orderid.toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("orderitem_u"+orderid.toString());
            re = new ArrayList<OrderItem>();
            for (Object id : ro) {
                OrderItem r =  getOrderItemByKey((Integer)id, url);
                if (r != null)
                    re.add(r);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
        }else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderitemuserpage", orderid);
            }else{
                RefreshUserDBD(orderid, true, true);
            }

            if(redisu.hasKey("orderitem_u"+orderid.toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("orderitem_u"+orderid.toString());
                re = new ArrayList<OrderItem>();
                for (Object id : ro) {
                    OrderItem r =  getOrderItemByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
            }
        }
        return re;
    }

    @Override
    public OrderItem getOrderItemByKey(Integer orderitemid, String url) {
        OrderItem re = null;
        Integer pageId = cacheService.PageID(orderitemid);
        if(!redisu.hHasKey(classname+"pageid", pageId.toString())) {
            //read redis
            re = (OrderItem) redisu.hget(classname, orderitemid.toString());
            redisu.hincr(classname+"pageid", pageId.toString(), 1);
        }else {         
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderitempage", pageId);
            }else{
                RefreshDBD(pageId, true);
            }

            if(redisu.hHasKey(classname, orderitemid.toString())) {
                //read redis
                re = (OrderItem) redisu.hget(classname, orderitemid.toString());
                redisu.hincr(classname+"pageid", pageId.toString(), 1);
            }
        }
        return re;
    }

    public void insertSelective_extra(OrderItem orderitem) {
        //add to OrderitemOrderDA
        RefreshUserDBD(orderitem.getOrderid(), false, false);
        BDBEnvironmentManager.getInstance();
        OrderitemOrderDA orderitemUserDA=new OrderitemOrderDA(BDBEnvironmentManager.getMyEntityStore());
        OrderitemOrder orderitemUser = orderitemUserDA.findOrderitemOrderById(orderitem.getOrderid());
        if(orderitemUser == null){
            List<Integer> orderitemIdList = new ArrayList<>();
            orderitemIdList.add(orderitem.getItemid());
            JSONArray jsonArray = JSONArray.fromObject(orderitemIdList);

            orderitemUser = new OrderitemOrder();
            orderitemUser.setOrderitemSize(1); 
            orderitemUser.setOrderitemList(jsonArray.toString());
            orderitemUser.setStatus(CacheService.STATUS_INSERT);
        }else{
            List<Integer> orderitemIdList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(orderitemUser.getOrderitemList());
            orderitemIdList = JSONArray.toList(jsonArray,Integer.class);
            orderitemIdList.add(orderitem.getItemid());

            orderitemUser.setOrderitemSize(orderitemUser.getOrderitemSize() + 1); 
            orderitemUser.setOrderitemList(jsonArray.toString());
            if(orderitemUser.getStatus() == null || orderitemUser.getStatus() == CacheService.STATUS_DELETE)
                orderitemUser.setStatus(CacheService.STATUS_UPDATE);
        }
        orderitemUserDA.saveOrderitemOrder(orderitemUser);

        //Re-publish to redis
        redisu.sAdd("orderitem_u" + orderitem.getOrderid().toString(), orderitem.getItemid()); 
    }

    @Override
    public void insertOrderItem(OrderItem orderitem) {
        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        Integer iid = (int) id;
        RefreshDBD(cacheService.PageID(iid), false);

        orderitem.setItemid(new Long(id).intValue());
        orderitem.setStatus(CacheService.STATUS_INSERT);
        orderitemDA.saveOrderItem(orderitem);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, orderitem.getItemid().toString(), (Object)orderitem, 0);

        insertSelective_extra(orderitem);
    }

    @Override
    public void TickBack_extra() {
        BDBEnvironmentManager.getInstance();
        OrderitemOrderDA orderitemUserDA=new OrderitemOrderDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                OrderitemOrder orderitemUser = orderitemUserDA.findOrderitemOrderById(i);
                if(orderitemUser != null){
                    if(null ==  orderitemUser.getStatus()) {
                        orderitemUserDA.removedOrderitemOrderById(orderitemUser.getOrderid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  orderitemUser.getStatus() && 1 == orderitemOrderMapper.deleteByPrimaryKey(orderitemUser.getOrderid())) {
                        orderitemUserDA.removedOrderitemOrderById(orderitemUser.getOrderid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  orderitemUser.getStatus()  && 1 == orderitemOrderMapper.insert(orderitemUser)) {
                        orderitemUserDA.removedOrderitemOrderById(orderitemUser.getOrderid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  orderitemUser.getStatus() && 1 == orderitemOrderMapper.updateByPrimaryKey(orderitemUser)) {
                        orderitemUserDA.removedOrderitemOrderById(orderitemUser.getOrderid());
                    }
                    redisu.del("orderitem_u"+orderitemUser.getOrderid().toString());
                }
            }
            redisu.hdel(classname_extra+"pageid", pageid.toString());
        }
        if (orderitemUserDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = cacheService.PageOut(classname);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                OrderItem orderitem = orderitemDA.findOrderItemById(i);
                if(orderitem != null){
                    if(null ==  orderitem.getStatus()) {
                        orderitemDA.removedOrderItemById(orderitem.getItemid());
                    }
        
                    if(CacheService.STATUS_DELETE ==  orderitem.getStatus() && 1 == orderItemMapper.deleteByPrimaryKey(orderitem.getItemid())) {
                        orderitemDA.removedOrderItemById(orderitem.getItemid());
                    }
        
                    if(CacheService.STATUS_INSERT ==  orderitem.getStatus()  && 1 == orderItemMapper.insert(orderitem)) {
                        orderitemDA.removedOrderItemById(orderitem.getItemid());
                    } 

                    if(CacheService.STATUS_UPDATE ==  orderitem.getStatus() && 1 == orderItemMapper.updateByPrimaryKey(orderitem)) {
                        orderitemDA.removedOrderItemById(orderitem.getItemid());
                    }
                    redisu.hdel(classname, orderitem.getItemid().toString());
                }
            }
            redisu.hdel(classname+"pageid", pageid.toString());
        }
        if (orderitemDA.IsEmpty()){
            cacheService.Archive(classname);
        }

        TickBack_extra();
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (cacheService.IsCache(classname, pageID)) {
            BDBEnvironmentManager.getInstance();
            OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<OrderItem> re = new ArrayList<OrderItem>();          
            OrderItemExample orderitemExample = new OrderItemExample();
            orderitemExample.or().andItemidGreaterThanOrEqualTo(cacheService.PageBegin(pageID));
            orderitemExample.or().andItemidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = orderItemMapper.selectByExample(orderitemExample);
            for (OrderItem value : re) {
                redisu.hset(classname, value.getItemid().toString(), value);
                orderitemDA.saveOrderItem(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
            quartzManager.addJob(classname,classname,classname,classname, OrderItemJob.class, null, job);
            redisu.hincr(classname+"pageid", pageID.toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname+"pageid", pageID.toString())) {
                BDBEnvironmentManager.getInstance();
                OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());

                Integer i = cacheService.PageBegin(pageID);
                Integer l = cacheService.PageEnd(pageID);
                for(;i < l; i++){
                    OrderItem r = orderitemDA.findOrderItemById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.hset(classname, i.toString(), r);   
                    }  
                }
                redisu.hincr(classname+"pageid", pageID.toString(), 1);
            }
        }    
    }

    @Override
    public void RefreshUserDBD(Integer orderid, boolean andAll, boolean refresRedis){
        BDBEnvironmentManager.getInstance();
        OrderitemOrderDA orderitemUserDA=new OrderitemOrderDA(BDBEnvironmentManager.getMyEntityStore());
        if (cacheService.IsCache(classname_extra,cacheService.PageID(orderid))) {
            /// init
            List<OrderitemOrder> re = new ArrayList<OrderitemOrder>();          
            OrderitemOrderExample orderitemOrderExample = new OrderitemOrderExample();
            orderitemOrderExample.or().andOrderidGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(orderid)));
            orderitemOrderExample.or().andOrderidLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(orderid)));

            re = orderitemOrderMapper.selectByExample(orderitemOrderExample);
            for (OrderitemOrder value : re) {
                orderitemUserDA.saveOrderitemOrder(value);

                List<Integer> orderitemIdList = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(value.getOrderitemList());
                orderitemIdList = JSONArray.toList(jsonArray, Integer.class);

                for(Integer orderitemId: orderitemIdList){
                    redisu.sAdd("orderitem_u"+value.getOrderid().toString(), (Object)orderitemId);
                }

                if(andAll && orderid == value.getOrderid() && value.getOrderitemSize() != 0){  
                    for(Integer orderitemId: orderitemIdList){
                        RefreshDBD(cacheService.PageID(orderitemId), refresRedis);
                    }
                }
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(orderid).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(orderid));
                Integer l = cacheService.PageEnd(cacheService.PageID(orderid));
                for(;i < l; i++){
                    OrderitemOrder r = orderitemUserDA.findOrderitemOrderById(i);
                    if(r!= null && r.getStatus() != CacheService.STATUS_DELETE){
                        redisu.sAdd("orderitem_u"+r.getOrderid().toString(), (Object)r.getOrderitemList()); 
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
            }
        }
    }
}
