package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ShopcartUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ShopcartUserDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, ShopcartUser> pIdx;// Primary Index

	public ShopcartUserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, ShopcartUser.class);
	}

	/**
* Add a ShopcartUser
	 */
	public void saveShopcartUser(ShopcartUser shopcartuser) {
		pIdx.put(shopcartuser);
	}

	/**
	 * Delete one based on user ID ShopcartUser
	 **/
	public void removedShopcartUserById(Integer shopcartuserId) {
		pIdx.delete(shopcartuserId);
	}

	/**
	 * Find one based on user IDShopcartUser
	 **/
	public ShopcartUser findShopcartUserById(Integer shopcartuserId) {
		return pIdx.get(shopcartuserId);
	}

	/**
	 * Find all ShopcartUser
	 **/
	public List<ShopcartUser> findAllShopcartUser() {
		List<ShopcartUser> shopcartuserList = new ArrayList<ShopcartUser>();
		// open cursor
		EntityCursor<ShopcartUser> shopcartuserCursorList = null;
		try {
			//Get the cursor
			shopcartuserCursorList = pIdx.entities();
			// Traversal cursor
			for (ShopcartUser shopcartuser : shopcartuserCursorList) {
				shopcartuserList.add(shopcartuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (shopcartuserCursorList != null) {
				// Close the cursor
				shopcartuserCursorList.close();
			}
		}
		return shopcartuserList;
	}
	
	
	/**
	 * Statistics of all users
	**/
	public Long findAllShopcartUserCount() {
		Long count = 0L;
		EntityCursor<ShopcartUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopcartUser shopcartuser : cursor) {
            	if(shopcartuser!=null) {
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
		EntityCursor<ShopcartUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopcartUser shopcartuser : cursor) {
            	if(shopcartuser!=null) {
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
