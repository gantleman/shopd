package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.OrderUser;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderUserDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, OrderUser> pIdx;// Primary Index

	public OrderUserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderUser.class);
	}

	/**
* Add a OrderUser
	 */
	public void saveOrderUser(OrderUser order) {
		pIdx.put(order);
	}

	/**
	 * Delete one based on user ID OrderUser
	 **/
	public void removedOrderUserById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * Find one based on user IDOrderUser
	 **/
	public OrderUser findOrderUserById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * Find all OrderUser
	 **/
	public List<OrderUser> findAllOrderUser() {
		List<OrderUser> orderList = new ArrayList<OrderUser>();
		// open cursor
		EntityCursor<OrderUser> orderCursorList = null;
		try {
			//Get the cursor
			orderCursorList = pIdx.entities();
			// Traversal cursor
			for (OrderUser order : orderCursorList) {
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
	 * Statistics of all users
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
