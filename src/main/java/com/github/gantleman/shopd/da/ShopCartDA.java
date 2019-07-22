package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ShopCart;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class ShopCartDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, ShopCart> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, ShopCart> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, ShopCart> sIdx2;// Secondary index

	public ShopCartDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, ShopCart.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
* Add a ShopCart
	 */
	public void saveShopCart(ShopCart shopcart) {
		pIdx.put(shopcart);
	}

	/**
	 * Delete one based on user ID ShopCart
	 **/
	public void removedShopCartById(Integer shopcartId) {
		pIdx.delete(shopcartId);
	}

	/**
	 * Delete one by user nameShopCart
	 **/
	public void removedShopCartByGoodsID(Integer goodsid) {
		sIdx.delete(goodsid);
	}

	/**
	 * Find one based on user IDShopCart
	 **/
	public ShopCart findShopCartById(Integer shopcartId) {
		return pIdx.get(shopcartId);
	}

	/**
	 * Find all ShopCart
	 **/
	public List<ShopCart> findAllShopCart() {
		List<ShopCart> shopcartList = new ArrayList<ShopCart>();
		// open cursor
		EntityCursor<ShopCart> shopcartCursorList = null;
		try {
			//Get the cursor
			shopcartCursorList = pIdx.entities();
			// Traversal cursor
			for (ShopCart shopcart : shopcartCursorList) {
				shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (shopcartCursorList != null) {
				// Close the cursor
				shopcartCursorList.close();
			}
		}
		return shopcartList;
	}
	
	/**
	 * 根据shopcartName查找所有的ShopCart
	 **/
	public List<ShopCart> findAllShopCartByGoodsID(Integer goodsid) {
	    
		List<ShopCart> shopcartList=new ArrayList<ShopCart>();
		
		EntityCursor<ShopCart> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(goodsid).entities();
			//Traversal cursor
			for (ShopCart shopcart : entityCursorList) {
				shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return shopcartList;
	}

	/**
	 * 根据shopcartName查找所有的ShopCart
	 **/
	public List<ShopCart> findAllShopCartByUGID(Integer userid, Integer goodsid) {
	    
		List<ShopCart> shopcartList=new ArrayList<ShopCart>();
		
		EntityCursor<ShopCart> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//Traversal cursor
			for (ShopCart shopcart : entityCursorList) {
				if(shopcart.getGoodsid() == goodsid)
					shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return shopcartList;
	}

	/**
	 * Statistics of all users
	**/
	public Long findAllShopCartCount() {
		Long count = 0L;
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopCart shopcart : cursor) {
            	if(shopcart!=null) {
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
	public Long findAllShopCartByGoodsIDCount(Integer goodsid) {
		Long count = 0L;
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = sIdx.subIndex(goodsid).entities();
            for (ShopCart shopcart : cursor) {
            	if(shopcart!=null) {
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
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopCart activity : cursor) {
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
