package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class OrderDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Order> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Order> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Date, Integer, Order> sIdx2;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Order> sIdx3;// 二级索引

	public OrderDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Order.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Date.class, "ordertime");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx3 = entityStore.getSecondaryIndex(pIdx, Integer.class, "addressid");
	}

	/**
	 * 添加一个Order
	 */
	public void saveOrder(Order order) {
		pIdx.put(order);
	}

	/**
	 * 根据用户Id删除一个Order
	 **/
	public void removedOrderById(Integer orderId) {
		pIdx.delete(orderId);
	}

	/**
	 * 根据userid删除一个Order
	 **/
	public void removedOrderByUserID(Integer userid) {
		sIdx.delete(userid);
	}

	/**
	 * 根据userid删除一个Order
	 **/
	public void removedOrderByOrderTime(Date ordertime) {
		sIdx2.delete(ordertime);
	}

	/**
	 * 根据userid删除一个Order
	 **/
	public void removedOrderByAddressID(Integer addressid) {
		sIdx3.delete(addressid);
	}
	
	/**
	 * 根据用户Id查找一个Order
	 **/
	public Order findOrderById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * 查找所有的Order
	 **/
	public List<Order> findAllOrder() {
		List<Order> orderList = new ArrayList<Order>();
		// 打开游标
		EntityCursor<Order> orderCursorList = null;
		try {
			//获取游标
			orderCursorList = pIdx.entities();
			// 遍历游标
			for (Order order : orderCursorList) {
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
	 * 根据orderName查找所有的Order
	 **/
	public List<Order> findAllOrderByUserID(Integer userid) {
	    
		List<Order> orderList=new ArrayList<Order>();
		
		EntityCursor<Order> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//遍历游标
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return orderList;
	}

	/**
	 * 根据orderName查找所有的Order
	 **/
	public List<Order> findAllOrderByOrderTime(Date ordertime) {
	    
		List<Order> orderList=new ArrayList<Order>();
		
		EntityCursor<Order> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(ordertime).entities();
			//遍历游标
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return orderList;
	}

	/**
	 * 根据orderName查找所有的Order
	 **/
	public List<Order> findAllOrderByAddressID(Integer addressid) {
	    
		List<Order> orderList=new ArrayList<Order>();
		
		EntityCursor<Order> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx3.subIndex(addressid).entities();
			//遍历游标
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return orderList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllOrderCount() {
		Long count = 0L;
		EntityCursor<Order> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Order order : cursor) {
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
	
	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllOrderByUserIDCount(Integer userid) {
		Long count = 0L;
		EntityCursor<Order> cursor = null;
        try{
            cursor = sIdx.subIndex(userid).entities();
            for (Order order : cursor) {
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

	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllOrderByOrderTimeCount(Date ordertime) {
		Long count = 0L;
		EntityCursor<Order> cursor = null;
        try{
            cursor = sIdx2.subIndex(ordertime).entities();
            for (Order order : cursor) {
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

	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllOrderByAddressIDCount(Integer addressid) {
		Long count = 0L;
		EntityCursor<Order> cursor = null;
        try{
            cursor = sIdx3.subIndex(addressid).entities();
            for (Order order : cursor) {
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

	public List<Order> findAllWhitStamp(long stamp) {
		List<Order> adminList = new ArrayList<Order>();
		// 打开游标
		EntityCursor<Order> adminCursorList = null;
		try {
			//获取游标
			adminCursorList = pIdx.entities();
			// 遍历游标
			for (Order order : adminCursorList) {
				if(order.getStamp() <= stamp) {
					adminList.add(order);
				}
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (adminCursorList != null) {
				// 关闭游标
				adminCursorList.close();
			}
		}
		return adminList;
	}

	public boolean IsEmpty() {
		boolean count = true;
		EntityCursor<Order> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Order activity : cursor) {
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
