package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.Orderisreceive;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderisreceiveDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Orderisreceive> pIdx;// 一级索引

	public OrderisreceiveDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Orderisreceive.class);
	}

	/**
	 * 添加一个Orderisreceive
	 */
	public void saveOrderisreceive(Orderisreceive order) {
		pIdx.put(order);
	}

	/**
	 * 根据用户Id删除一个Orderisreceive
	 **/
	public void removedOrderisreceiveById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * 根据用户Id查找一个Orderisreceive
	 **/
	public Orderisreceive findOrderisreceiveById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * 查找所有的Orderisreceive
	 **/
	public List<Orderisreceive> findAllOrderisreceive() {
		List<Orderisreceive> orderList = new ArrayList<Orderisreceive>();
		// 打开游标
		EntityCursor<Orderisreceive> orderCursorList = null;
		try {
			//获取游标
			orderCursorList = pIdx.entities();
			// 遍历游标
			for (Orderisreceive order : orderCursorList) {
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
	public Long findAllOrderisreceiveCount() {
		Long count = 0L;
		EntityCursor<Orderisreceive> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Orderisreceive order : cursor) {
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
		EntityCursor<Orderisreceive> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Orderisreceive activity : cursor) {
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
