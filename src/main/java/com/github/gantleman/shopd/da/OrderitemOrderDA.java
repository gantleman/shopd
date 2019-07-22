package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.OrderitemOrder;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class OrderitemOrderDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, OrderitemOrder> pIdx;// Primary Index

	public OrderitemOrderDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderitemOrder.class);
	}

	/**
* Add a OrderitemOrder
	 */
	public void saveOrderitemOrder(OrderitemOrder orderitemorder) {
		pIdx.put(orderitemorder);
	}

	/**
	 * Delete one based on user ID OrderitemOrder
	 **/
	public void removedOrderitemOrderById(Integer orderitemorderId) {
		pIdx.delete(orderitemorderId);
	}

	/**
	 * Find one based on user IDOrderitemOrder
	 **/
	public OrderitemOrder findOrderitemOrderById(Integer orderitemorderId) {
		return pIdx.get(orderitemorderId);
	}

	/**
	 * Find all OrderitemOrder
	 **/
	public List<OrderitemOrder> findAllOrderitemOrder() {
		List<OrderitemOrder> orderitemorderList = new ArrayList<OrderitemOrder>();
		// open cursor
		EntityCursor<OrderitemOrder> orderitemorderCursorList = null;
		try {
			//Get the cursor
			orderitemorderCursorList = pIdx.entities();
			// Traversal cursor
			for (OrderitemOrder orderitemorder : orderitemorderCursorList) {
				orderitemorderList.add(orderitemorder);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderitemorderCursorList != null) {
				// Close the cursor
				orderitemorderCursorList.close();
			}
		}
		return orderitemorderList;
	}
	
	
	/**
	 * Statistics of all users
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
