package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.OrderUser;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderUserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, OrderUser> pIdx;// 一级索引

	public OrderUserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderUser.class);
	}

	/**
	 * 添加一个OrderUser
	 */
	public void saveOrderUser(OrderUser order) {
		pIdx.put(order);
	}

	/**
	 * 根据用户Id删除一个OrderUser
	 **/
	public void removedOrderUserById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * 根据用户Id查找一个OrderUser
	 **/
	public OrderUser findOrderUserById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * 查找所有的OrderUser
	 **/
	public List<OrderUser> findAllOrderUser() {
		List<OrderUser> orderList = new ArrayList<OrderUser>();
		// 打开游标
		EntityCursor<OrderUser> orderCursorList = null;
		try {
			//获取游标
			orderCursorList = pIdx.entities();
			// 遍历游标
			for (OrderUser order : orderCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderCursorList != null) {
				// 关闭游标
				orderCursorList.close();
			}
		}
		return orderList;
	}
	/**
	 * 统计所有用户数
	**/
	public Long findAllOrderUserCount() {
		Long count = 0L;
		EntityCursor<OrderUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderUser order : cursor) {
            	if(order!=null) {
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
		EntityCursor<OrderUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderUser activity : cursor) {
            	if(activity!=null) {
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
