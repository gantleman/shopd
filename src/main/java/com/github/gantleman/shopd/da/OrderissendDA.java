package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.Orderissend;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderissendDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Orderissend> pIdx;// Primary Index

	public OrderissendDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Orderissend.class);
	}

	/**
* Add a Orderissend
	 */
	public void saveOrderissend(Orderissend order) {
		pIdx.put(order);
	}

	/**
	 * Delete one based on user ID Orderissend
	 **/
	public void removedOrderissendById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * Find one based on user IDOrderissend
	 **/
	public Orderissend findOrderissendById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * Find all Orderissend
	 **/
	public List<Orderissend> findAllOrderissend() {
		List<Orderissend> orderList = new ArrayList<Orderissend>();
		// open cursor
		EntityCursor<Orderissend> orderCursorList = null;
		try {
			//Get the cursor
			orderCursorList = pIdx.entities();
			// Traversal cursor
			for (Orderissend order : orderCursorList) {
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
