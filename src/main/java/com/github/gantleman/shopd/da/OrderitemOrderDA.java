package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.OrderitemOrder;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class OrderitemOrderDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, OrderitemOrder> pIdx;// 一级索引

	public OrderitemOrderDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderitemOrder.class);
	}

	/**
	 * 添加一个OrderitemOrder
	 */
	public void saveOrderitemOrder(OrderitemOrder orderitemorder) {
		pIdx.put(orderitemorder);
	}

	/**
	 * 根据用户Id删除一个OrderitemOrder
	 **/
	public void removedOrderitemOrderById(Integer orderitemorderId) {
		pIdx.delete(orderitemorderId);
	}

	/**
	 * 根据用户Id查找一个OrderitemOrder
	 **/
	public OrderitemOrder findOrderitemOrderById(Integer orderitemorderId) {
		return pIdx.get(orderitemorderId);
	}

	/**
	 * 查找所有的OrderitemOrder
	 **/
	public List<OrderitemOrder> findAllOrderitemOrder() {
		List<OrderitemOrder> orderitemorderList = new ArrayList<OrderitemOrder>();
		// 打开游标
		EntityCursor<OrderitemOrder> orderitemorderCursorList = null;
		try {
			//获取游标
			orderitemorderCursorList = pIdx.entities();
			// 遍历游标
			for (OrderitemOrder orderitemorder : orderitemorderCursorList) {
				orderitemorderList.add(orderitemorder);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderitemorderCursorList != null) {
				// 关闭游标
				orderitemorderCursorList.close();
			}
		}
		return orderitemorderList;
	}
	
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllOrderitemOrderCount() {
		Long count = 0L;
		EntityCursor<OrderitemOrder> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderitemOrder orderitemorder : cursor) {
            	if(orderitemorder!=null) {
            		count++;
            	}
			}
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
		return count;
	}

	public boolean IsEmpty() {
		boolean count = true;
		EntityCursor<OrderitemOrder> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderitemOrder orderitemorder : cursor) {
            	if(orderitemorder!=null) {
					count = false;
					break;
            	}
			}
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
		return count;
	}
}
