package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class OrderItemDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, OrderItem> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, OrderItem> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, OrderItem> sIdx2;// 二级索引

	public OrderItemDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderItem.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "orderid");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
	}

	/**
	 * 添加一个OrderItem
	 */
	public void saveOrderItem(OrderItem orderitem) {
		pIdx.put(orderitem);
	}

	/**
	 * 根据用户Id删除一个OrderItem
	 **/
	public void removedOrderItemById(Integer orderitemId) {
		pIdx.delete(orderitemId);
	}

	/**
	 * 根据用户名称删除一个OrderItem
	 **/
	public void removedOrderItemByOrderID(Integer orderid) {
		sIdx.delete(orderid);
	}

	/**
	 * 根据用户名称删除一个OrderItem
	 **/
	public void removedOrderItemByGoodsID(Integer goodsid) {
		sIdx2.delete(goodsid);
	}

	/**
	 * 根据用户Id查找一个OrderItem
	 **/
	public OrderItem findOrderItemById(Integer orderitemId) {
		return pIdx.get(orderitemId);
	}

	/**
	 * 查找所有的OrderItem
	 **/
	public List<OrderItem> findAllOrderItem() {
		List<OrderItem> orderitemList = new ArrayList<OrderItem>();
		// 打开游标
		EntityCursor<OrderItem> orderitemCursorList = null;
		try {
			//获取游标
			orderitemCursorList = pIdx.entities();
			// 遍历游标
			for (OrderItem orderitem : orderitemCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderitemCursorList != null) {
				// 关闭游标
				orderitemCursorList.close();
			}
		}
		return orderitemList;
	}
	
	/**
	 * 根据orderitemName查找所有的OrderItem
	 **/
	public List<OrderItem> findAllOrderItemByOrderID(Integer orderid) {
	    
		List<OrderItem> orderitemList=new ArrayList<OrderItem>();
		
		EntityCursor<OrderItem> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(orderid).entities();
			//遍历游标
			for (OrderItem orderitem : entityCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return orderitemList;
	}

	/**
	 * 根据orderitemName查找所有的OrderItem
	 **/
	public List<OrderItem> findAllOrderItemByGoodsID(Integer goodsid) {
	    
		List<OrderItem> orderitemList=new ArrayList<OrderItem>();
		
		EntityCursor<OrderItem> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(goodsid).entities();
			//遍历游标
			for (OrderItem orderitem : entityCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return orderitemList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllOrderItemCount() {
		Long count = 0L;
		EntityCursor<OrderItem> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderItem orderitem : cursor) {
            	if(orderitem!=null) {
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
	
	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllOrderItemByOrderIDCount(Integer orderid) {
		Long count = 0L;
		EntityCursor<OrderItem> cursor = null;
        try{
            cursor = sIdx.subIndex(orderid).entities();
            for (OrderItem orderitem : cursor) {
            	if(orderitem!=null) {
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

	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllOrderItemByGoodsIDCount(Integer goodsid) {
		Long count = 0L;
		EntityCursor<OrderItem> cursor = null;
        try{
            cursor = sIdx2.subIndex(goodsid).entities();
            for (OrderItem orderitem : cursor) {
            	if(orderitem!=null) {
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
		EntityCursor<OrderItem> cursor = null;
        try{
            cursor = pIdx.entities();
            for (OrderItem activity : cursor) {
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
