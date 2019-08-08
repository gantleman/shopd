package com.github.gantleman.shopd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.github.gantleman.shopd.da.OrderItemDA;
import com.github.gantleman.shopd.da.OrderitemOrderDA;
import com.github.gantleman.shopd.dao.OrderItemMapper;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.entity.OrderitemOrder;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.jobs.OrderItemJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderitemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

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
        List<OrderItem> re = new ArrayList<OrderItem>();

        if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(orderid).toString())) {
            //read redis
            Set<Object> ro = redisu.sGet("orderitem_u"+orderid.toString());
            if(ro != null){
                for (Object id : ro) {
                    OrderItem r =  getOrderItemByKey((Integer)id, url);
                    if (r != null)
                        re.add(r);
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
            }
        } else {
            if(!cacheService.IsLocal(url)){
                cacheService.RemoteRefresh("/orderitemuserpage", orderid);
            }else{
                RefreshUserDBD(orderid, true, true);
            }

            if(redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(orderid).toString())) {
                //read redis
                Set<Object> ro = redisu.sGet("orderitem_u"+orderid.toString());
                if(ro != null){
                    for (Object id : ro) {
                        OrderItem r =  getOrderItemByKey((Integer)id, url);
                        if (r != null)
                            re.add(r);
                    }
                    redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
                }
            }
        }
        return re;
    }

    @Override
    public OrderItem getOrderItemByKey(Integer orderitemid, String url) {
        OrderItem re = null;
        Integer pageId = cacheService.PageID(orderitemid);
        if(redisu.hHasKey(classname+"pageid", pageId.toString())) {
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
        OrderitemOrder orderitemOrder = orderitemUserDA.findOrderitemOrderById(orderitem.getOrderid());
        if(orderitemOrder == null){
            orderitemOrder = new OrderitemOrder();
        }
        orderitemOrder.addOrderitemList(orderitem.getItemid());
        orderitemUserDA.saveOrderitemOrder(orderitemOrder);

        //Re-publish to redis
        redisu.sAdd("orderitem_u" + orderitem.getOrderid().toString(), orderitem.getItemid()); 
    }

    @Override
    public void insertOrderItem(OrderItem orderitem) {
        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.EventCteate(classname);
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
    public void Clean_extra(Boolean all) {
        BDBEnvironmentManager.getInstance();
        OrderitemOrderDA orderitemUserDA=new OrderitemOrderDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname_extra):cacheService.PageOut(classname_extra);
        for(Integer pageid : listid){
            int l = cacheService.PageEnd(pageid);
            for(int i=cacheService.PageBegin(pageid); i<l; i++ ){
                OrderitemOrder orderitemUser = orderitemUserDA.findOrderitemOrderById(i);
                if(orderitemUser != null){
                    orderitemUserDA.removedOrderitemOrderById(orderitemUser.getOrderid());
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
    public void Clean(Boolean all) {
        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());
        List<Integer> listid = all?cacheService.PageGetAll(classname):cacheService.PageOut(classname);
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

        Clean_extra(all);
    }

    @Override
    public void RefreshDBD(Integer pageID, boolean refresRedis) {
        if (!cacheService.IsCache(classname, pageID, classname, OrderItemJob.class, job)) {
            BDBEnvironmentManager.getInstance();
            OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());
            ///init
            List<OrderItem> re = new ArrayList<OrderItem>();          
            OrderItemExample orderitemExample = new OrderItemExample();
            orderitemExample.or().andItemidGreaterThanOrEqualTo(cacheService.PageBegin(pageID))
            .andItemidLessThanOrEqualTo(cacheService.PageEnd(pageID));

            re = orderItemMapper.selectByExample(orderitemExample);
            for (OrderItem value : re) {
                redisu.hset(classname, value.getItemid().toString(), value);
                orderitemDA.saveOrderItem(value);
            }

            BDBEnvironmentManager.getMyEntityStore().sync();
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
        if (!cacheService.IsCache(classname_extra,cacheService.PageID(orderid))) {
            /// init
            List<OrderItem> re = new ArrayList<OrderItem>();          
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.or().andOrderidGreaterThanOrEqualTo(cacheService.PageBegin(cacheService.PageID(orderid)))
            .andOrderidLessThanOrEqualTo(cacheService.PageEnd(cacheService.PageID(orderid)));

            re = orderItemMapper.selectByExample(orderItemExample);
            for (OrderItem value : re) {
                OrderitemOrder orderitemOrder  = orderitemUserDA.findOrderitemOrderById(value.getItemid());
                if(orderitemOrder == null){
                    orderitemOrder = new OrderitemOrder();
                }
                
                redisu.sAdd("orderitem_u"+value.getOrderid().toString(), (Object)value.getItemid());

                if(andAll){ 
                    RefreshDBD(cacheService.PageID(value.getItemid()), refresRedis);
                }

                orderitemUserDA.saveOrderitemOrder(orderitemOrder);
            }
            redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
        }else if(refresRedis){
            if(!redisu.hHasKey(classname_extra+"pageid", cacheService.PageID(orderid).toString())) {
                Integer i = cacheService.PageBegin(cacheService.PageID(orderid));
                Integer l = cacheService.PageEnd(cacheService.PageID(orderid));
                for(;i < l; i++){
                    OrderitemOrder r = orderitemUserDA.findOrderitemOrderById(i);
                    if(r!= null){
                        List<Integer> li = r.getOrderitemList();
                        for(Integer id: li){
                            redisu.sAdd("orderitem_u"+r.getOrderid().toString(), (Object)id);
                        }
                    }                
                }
                redisu.hincr(classname_extra+"pageid", cacheService.PageID(orderid).toString(), 1);
            }
        }
    }
}
