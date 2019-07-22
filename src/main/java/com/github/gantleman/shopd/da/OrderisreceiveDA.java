package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import  com.github.gantleman.shopd.entity.Orderisreceive;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class OrderisreceiveDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Orderisreceive> pIdx;// Primary Index

	public OrderisreceiveDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Orderisreceive.class);
	}

	/**
* Add a Orderisreceive
	 */
	public void saveOrderisreceive(Orderisreceive order) {
		pIdx.put(order);
	}

	/**
	 * Delete one based on user ID Orderisreceive
	 **/
	public void removedOrderisreceiveById(Integer orderId) {
		pIdx.delete(orderId);
	}
	
	/**
	 * Find one based on user IDOrderisreceive
	 **/
	public Orderisreceive findOrderisreceiveById(Integer orderId) {
		return pIdx.get(orderId);
	}

	/**
	 * Find all Orderisreceive
	 **/
	public List<Orderisreceive> findAllOrderisreceive() {
		List<Orderisreceive> orderList = new ArrayList<Orderisreceive>();
		// open cursor
		EntityCursor<Orderisreceive> orderCursorList = null;
		try {
			//Get the cursor
			orderCursorList = pIdx.entities();
			// Traversal cursor
			for (Orderisreceive order : orderCursorList) {
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
