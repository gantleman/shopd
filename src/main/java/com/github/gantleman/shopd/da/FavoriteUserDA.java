package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.FavoriteUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class FavoriteUserDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, FavoriteUser> pIdx;// Primary Index

	public FavoriteUserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, FavoriteUser.class);
	}

	/**
* Add a FavoriteUser
	 */
	public void saveFavoriteUser(FavoriteUser favoriteuser) {
		pIdx.put(favoriteuser);
	}

	/**
	 * Delete one based on user ID FavoriteUser
	 **/
	public void removedFavoriteUserById(Integer favoriteuserId) {
		pIdx.delete(favoriteuserId);
	}

	/**
	 * Find one based on user IDFavoriteUser
	 **/
	public FavoriteUser findFavoriteUserById(Integer favoriteuserId) {
		return pIdx.get(favoriteuserId);
	}

	/**
	 * Find all FavoriteUser
	 **/
	public List<FavoriteUser> findAllFavoriteUser() {
		List<FavoriteUser> favoriteuserList = new ArrayList<FavoriteUser>();
		// open cursor
		EntityCursor<FavoriteUser> favoriteuserCursorList = null;
		try {
			//Get the cursor
			favoriteuserCursorList = pIdx.entities();
			// Traversal cursor
			for (FavoriteUser favoriteuser : favoriteuserCursorList) {
				favoriteuserList.add(favoriteuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (favoriteuserCursorList != null) {
				// Close the cursor
				favoriteuserCursorList.close();
			}
		}
		return favoriteuserList;
	}
	
	
	/**
	 * Statistics of all users
	**/
	public Long findAllFavoriteUserCount() {
		Long count = 0L;
		EntityCursor<FavoriteUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (FavoriteUser favoriteuser : cursor) {
            	if(favoriteuser!=null) {
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
		EntityCursor<FavoriteUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (FavoriteUser favoriteuser : cursor) {
            	if(favoriteuser!=null) {
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
