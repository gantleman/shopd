package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.Orderissend;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderissendDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Orderissend> pIdx;// 一级索引

	public OrderissendDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Orderissend.class);
	}

	/**
	 * 添加一个Orderissend
	 */
	public void saveOrderissend(Orderissend order) {
		pIdx.put(order);
	}

	/**
	 * 根据用户Id删除一个Orderissend
	 **/
	public void removedOrderissendById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * 根据用户Id查找一个Orderissend
	 **/
	public Orderissend findOrderissendById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * 查找所有的Orderissend
	 **/
	public List<Orderissend> findAllOrderissend() {
		List<Orderissend> orderList = new ArrayList<Orderissend>();
		// 打开游标
		EntityCursor<Orderissend> orderCursorList = null;
		try {
			//获取游标
			orderCursorList = pIdx.entities();
			// 遍历游标
			for (Orderissend order : orderCursorList) {
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
	public Long findAllOrderissendCount() {
		Long count = 0L;
		EntityCursor<Orderissend> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Orderissend order : cursor) {
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
		EntityCursor<Orderissend> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Orderissend activity : cursor) {
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
