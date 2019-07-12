package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.da.OrderItemDA;
import com.github.gantleman.shopd.dao.OrderItemMapper;
import com.github.gantleman.shopd.entity.OrderItem;
import com.github.gantleman.shopd.entity.OrderItemExample;
import com.github.gantleman.shopd.service.CacheService;
import com.github.gantleman.shopd.service.OrderItemService;
import com.github.gantleman.shopd.service.jobs.OrderItemJob;
import com.github.gantleman.shopd.util.BDBEnvironmentManager;
import com.github.gantleman.shopd.util.QuartzManager;
import com.github.gantleman.shopd.util.RedisUtil;
import com.github.gantleman.shopd.util.TimeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service("orderitemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired(required = false)
    private RedisUtil redisu;

    @Value("${srping.quartz.exprie}")
    Integer exprie;

    @Autowired
    private OrderItemJob job;

    @Autowired
    private QuartzManager quartzManager;

    private String classname = "OrderItem";

    @Value("${srping.cache.page}")
    Integer page;

    @PostConstruct
    public void init() {
        if (cacheService.IsCache(classname)) {
            ///create time
            quartzManager.addJob(classname,classname,classname,classname, OrderItemJob.class, null, job);
        }
    } 

    @Override
    public List<OrderItem> getOrderItemByOrderId(Integer orderid) {

        List<OrderItem> re = new ArrayList<OrderItem>();
        if(redisu.hasKey("OrderItem_n"+orderid)) {

            //read redis
            Set<Object> rm = redisu.sGet("OrderItem_n"+orderid);

            for(Object value: rm) {
                Object hr = redisu.hget(classname, ((Integer)value).toString());
                if(hr != null)
                    re.add((OrderItem)hr);
            }
            redisu.expire("OrderItem_n"+orderid, 0);
            redisu.expire(classname, 0);
        }else {
            if(redisu.hasKey(classname)){
                //write redis
                Map<String, Object> tmap = new HashMap<>();
                List<OrderItem> lre = new ArrayList<OrderItem>();
                lre = orderItemMapper.selectByExample(new OrderItemExample());
                for (OrderItem value : lre) {
                    tmap.put(value.getItemid().toString(), (Object)value);
                    redisu.sAddAndTime("OrderItem_n"+value.getGoodsid(), 0, value.getItemid());
                    re.add(value);
                }
                ///read and write
                if(!redisu.hasKey(classname)) {
                    redisu.hmset(classname, tmap, 0);
                }                 
            }
        }
        return re;
    }

    @Override
    public void insertOrderItem(OrderItem orderitem) {
        RefreshDBD();

        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());

        long id = cacheService.eventCteate(classname);
        orderitem.setItemid(new Long(id).intValue());
        orderitem.MakeStamp();
        orderitem.setStatus(2);
        orderitemDA.saveOrderItem(orderitem);
        BDBEnvironmentManager.getMyEntityStore().sync();

        //Re-publish to redis
        redisu.hset(classname, orderitem.getItemid().toString(), orderitem, 0);
    }


    @Override
    public void TickBack() {
        BDBEnvironmentManager.getInstance();
        OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());
        List<OrderItem> lorderitem = orderitemDA.findAllWhitStamp(TimeUtils.getTimeWhitLong() - exprie);

        for (OrderItem orderitem : lorderitem) {
            if(null ==  orderitem.getStatus()) {
                orderitemDA.removedOrderItemById(orderitem.getItemid());
            }

            if(1 ==  orderitem.getStatus() && 1 == orderItemMapper.deleteByPrimaryKey(orderitem.getItemid())) {
                orderitemDA.removedOrderItemById(orderitem.getItemid());
            }

            if(2 ==  orderitem.getStatus()  && 1 == orderItemMapper.insert(orderitem)) {
                orderitemDA.removedOrderItemById(orderitem.getItemid());
            }

            if(3 ==  orderitem.getStatus() && 1 == orderItemMapper.updateByPrimaryKey(orderitem)) {
                orderitemDA.removedOrderItemById(orderitem.getItemid());
            }
        }

        if (orderitemDA.IsEmpty()){
            cacheService.Archive(classname);
        }
    }

    public void RefreshDBD() {
        ///init
       if (cacheService.IsCache(classname)) {
           BDBEnvironmentManager.getInstance();
           OrderItemDA orderitemDA=new OrderItemDA(BDBEnvironmentManager.getMyEntityStore());

           Set<Integer> id = new HashSet<Integer>();
           List<OrderItem> re = new ArrayList<OrderItem>();

           OrderItemExample orderitemExample = new OrderItemExample();
           re = orderItemMapper.selectByExample(orderitemExample);
           for (OrderItem value : re) {
               value.MakeStamp();
               orderitemDA.saveOrderItem(value);

               redisu.sAddAndTime("OrderItem_n"+value.getGoodsid(), 0, value.getItemid());
               redisu.hset(classname, value.getItemid().toString(), value, 0);
           }

           BDBEnvironmentManager.getMyEntityStore().sync();
           
           if(cacheService.IsCache(classname)){         
               quartzManager.addJob(classname,classname,classname,classname, OrderItemJob.class, null, job);          
           }
       }
   }
}
