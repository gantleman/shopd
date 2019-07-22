package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Favorite;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class FavoriteDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Favorite> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Favorite> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Favorite> sIdx2;// Secondary index

	public FavoriteDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Favorite.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
* Add a Favorite
	 */
	public void saveFavorite(Favorite favorite) {
		pIdx.put(favorite);
	}

	/**
	 * Delete one based on user ID Favorite
	 **/
	public void removedFavoriteById(Integer favoriteId) {
		pIdx.delete(favoriteId);
	}

	/**
	 * Delete one by user nameFavorite
	 **/
	public void removedFavoriteByGoodsID(Integer goodsid) {
		sIdx.delete(goodsid);
	}

		/**
	 * Delete one by user nameFavorite
	 **/
	public void removedFavoriteByUserID(Integer Userid) {
		sIdx2.delete(Userid);
	}

	/**
	 * Find one based on user IDFavorite
	 **/
	public Favorite findFavoriteById(Integer favoriteId) {
		return pIdx.get(favoriteId);
	}

	/**
	 * Find all Favorite
	 **/
	public List<Favorite> findAllFavorite() {
		List<Favorite> favoriteList = new ArrayList<Favorite>();
		// open cursor
		EntityCursor<Favorite> favoriteCursorList = null;
		try {
			//Get the cursor
			favoriteCursorList = pIdx.entities();
			// Traversal cursor
			for (Favorite favorite : favoriteCursorList) {
				favoriteList.add(favorite);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (favoriteCursorList != null) {
				// Close the cursor
				favoriteCursorList.close();
			}
		}
		return favoriteList;
	}
	
	/**
	 * 根据favoriteName查找所有的Favorite
	 **/
	public List<Favorite> findAllFavoriteByUserID(Integer userid) {
	    
		List<Favorite> favoriteList=new ArrayList<Favorite>();
		
		EntityCursor<Favorite> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//Traversal cursor
			for (Favorite favorite : entityCursorList) {
				favoriteList.add(favorite);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return favoriteList;
	}
	
	/**
	 * Statistics of all users
	**/
	public Long findAllFavoriteCount() {
		Long count = 0L;
		EntityCursor<Favorite> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Favorite favorite : cursor) {
            	if(favorite!=null) {
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
	public Long findAllFavoriteByGoodsIDCount(Integer goodsid) {
		Long count = 0L;
		EntityCursor<Favorite> cursor = null;
        try{
            cursor = sIdx.subIndex(goodsid).entities();
            for (Favorite favorite : cursor) {
            	if(favorite!=null) {
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
		EntityCursor<Favorite> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Favorite activity : cursor) {
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
