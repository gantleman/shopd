package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class OrderDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Order> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Order> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Date, Integer, Order> sIdx2;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Order> sIdx3;// Secondary index

	public OrderDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Order.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Date.class, "ordertime");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx3 = entityStore.getSecondaryIndex(pIdx, Integer.class, "addressid");
	}

	/**
* Add a Order
	 */
	public void saveOrder(Order order) {
		pIdx.put(order);
	}

	/**
	 * Delete one based on user ID Order
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
	 * Find one based on user IDOrder
	 **/
	public Order findOrderById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * Find all Order
	 **/
	public List<Order> findAllOrder() {
		List<Order> orderList = new ArrayList<Order>();
		// open cursor
		EntityCursor<Order> orderCursorList = null;
		try {
			//Get the cursor
			orderCursorList = pIdx.entities();
			// Traversal cursor
			for (Order order : orderCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderCursorList != null) {
				// Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//Traversal cursor
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(ordertime).entities();
			//Traversal cursor
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx3.subIndex(addressid).entities();
			//Traversal cursor
			for (Order order : entityCursorList) {
				orderList.add(order);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return orderList;
	}
	
	/**
	 * Statistics of all users
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
	 *Statistics the total number of users who satisfy the username
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
	 *Statistics the total number of users who satisfy the username
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
	 *Statistics the total number of users who satisfy the username
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
