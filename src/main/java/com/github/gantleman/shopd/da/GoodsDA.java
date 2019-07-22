package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Goods;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class GoodsDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Goods> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Goods> sIdx;// Secondary index
	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<String, Integer, Goods> sIdx2;// Secondary index

	public GoodsDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Goods.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "category");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, String.class, "goodsname");
	}

	/**
* Add a Goods
	 */
	public void saveGoods(Goods goods) {
		pIdx.put(goods);
	}

	/**
	 * Delete one based on user ID Goods
	 **/
	public void removedGoodsById(Integer goodsId) {
		pIdx.delete(goodsId);
	}

	/**
	 * Delete one by user nameGoods
	 **/
	public void removedGoodsByActivityID(Integer activityid) {
		sIdx.delete(activityid);
	}
	
	/**
	 * Delete one by user nameGoods
	 **/
	public void removedGoodsByCategory(Integer category) {
		sIdx.delete(category);
	}

	/**
	 * Find one based on user IDGoods
	 **/
	public Goods findGoodsById(Integer goodsId) {
		return pIdx.get(goodsId);
	}

	/**
	 * Find all Goods
	 **/
	public List<Goods> findAllGoods() {
		List<Goods> goodsList = new ArrayList<Goods>();
		// open cursor
		EntityCursor<Goods> goodsCursorList = null;
		try {
			//Get the cursor
			goodsCursorList = pIdx.entities();
			// Traversal cursor
			for (Goods goods : goodsCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (goodsCursorList != null) {
				// Close the cursor
				goodsCursorList.close();
			}
		}
		return goodsList;
	}
	
	/**
	 * 根据goodsName查找所有的Goods
	 **/
	public List<Goods> findAllGoodsByCategory(Integer category) {
	    
		List<Goods> goodsList=new ArrayList<Goods>();
		
		EntityCursor<Goods> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(category).entities();
			//Traversal cursor
			for (Goods goods : entityCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return goodsList;
	}

	/**
	 * 根据goodsName查找所有的Goods
	 **/
	public List<Goods> findAllGoodsByGoodsname(String name) {
	    
		List<Goods> goodsList=new ArrayList<Goods>();
		
		EntityCursor<Goods> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(name).entities();
			//Traversal cursor
			for (Goods goods : entityCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return goodsList;
	}

	/**
	 * Statistics of all users
	**/
	public Long findAllGoodsCount() {
		Long count = 0L;
		EntityCursor<Goods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Goods goods : cursor) {
            	if(goods!=null) {
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
	public Long findAllGoodsByCategoryCount(Integer category) {
		Long count = 0L;
		EntityCursor<Goods> cursor = null;
        try{
            cursor = sIdx.subIndex(category).entities();
            for (Goods goods : cursor) {
            	if(goods!=null) {
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
		EntityCursor<Goods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Goods activity : cursor) {
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
