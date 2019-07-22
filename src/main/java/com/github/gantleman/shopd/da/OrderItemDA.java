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

	// Primary key field type, entity class
	PrimaryIndex<Integer, OrderItem> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, OrderItem> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, OrderItem> sIdx2;// Secondary index

	public OrderItemDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, OrderItem.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "orderid");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
	}

	/**
* Add a OrderItem
	 */
	public void saveOrderItem(OrderItem orderitem) {
		pIdx.put(orderitem);
	}

	/**
	 * Delete one based on user ID OrderItem
	 **/
	public void removedOrderItemById(Integer orderitemId) {
		pIdx.delete(orderitemId);
	}

	/**
	 * Delete one by user nameOrderItem
	 **/
	public void removedOrderItemByOrderID(Integer orderid) {
		sIdx.delete(orderid);
	}

	/**
	 * Delete one by user nameOrderItem
	 **/
	public void removedOrderItemByGoodsID(Integer goodsid) {
		sIdx2.delete(goodsid);
	}

	/**
	 * Find one based on user IDOrderItem
	 **/
	public OrderItem findOrderItemById(Integer orderitemId) {
		return pIdx.get(orderitemId);
	}

	/**
	 * Find all OrderItem
	 **/
	public List<OrderItem> findAllOrderItem() {
		List<OrderItem> orderitemList = new ArrayList<OrderItem>();
		// open cursor
		EntityCursor<OrderItem> orderitemCursorList = null;
		try {
			//Get the cursor
			orderitemCursorList = pIdx.entities();
			// Traversal cursor
			for (OrderItem orderitem : orderitemCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (orderitemCursorList != null) {
				// Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(orderid).entities();
			//Traversal cursor
			for (OrderItem orderitem : entityCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(goodsid).entities();
			//Traversal cursor
			for (OrderItem orderitem : entityCursorList) {
				orderitemList.add(orderitem);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return orderitemList;
	}
	
	/**
	 * Statistics of all users
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
	 *Statistics the total number of users who satisfy the username
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
	 *Statistics the total number of users who satisfy the username
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
