package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Cache;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class CacheDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Cache> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<String, Integer, Cache> sIdx;// Secondary index

	public CacheDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Cache.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "cName");
	}

	/**
* Add a Cache
	 */
	public void saveCache(Cache cache) {
		pIdx.put(cache);
	}

	/**
	 * Delete one based on user ID Cache
	 **/
	public void removedCacheById(Integer cacheId) {
		pIdx.delete(cacheId);
	}

	/**
	 * 根据用户名称删除Address
	 **/
	public void removedAddressByName(String name) {
		sIdx.delete(name);
	}


	/**
	 * Find one based on user IDCache
	 **/
	public Cache findCacheById(Integer cacheId) {
		return pIdx.get(cacheId);
	}

	/**
	 * Find one based on user IDCache
	 **/
	public Cache findCacheByName(String name) {
		return sIdx.get(name);
	}

	/**
	 * Find all Cache
	 **/
	public List<Cache> findAllCache() {
		List<Cache> cacheList = new ArrayList<Cache>();
		// open cursor
		EntityCursor<Cache> cacheCursorList = null;
		try {
			//Get the cursor
			cacheCursorList = pIdx.entities();
			// Traversal cursor
			for (Cache cache : cacheCursorList) {
				cacheList.add(cache);
			}
		} catch (DatabaseException e) {
		} finally {
			if (cacheCursorList != null) {
				// Close the cursor
				cacheCursorList.close();
			}
		}
		return cacheList;
	}

	/**
	 * 根据addressName查找所有的Address
	 **/
	public List<Cache> findAllAddressByUserID(String name) {
	    
		List<Cache> nameList=new ArrayList<Cache>();
		
		EntityCursor<Cache> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(name).entities();
			//Traversal cursor
			for (Cache address : entityCursorList) {
				nameList.add(address);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return nameList;
	}

	/**
	 * Statistics of all users
	**/
	public Long findAllCacheCount() {
		Long count = 0L;
		EntityCursor<Cache> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Cache cache : cursor) {
            	if(cache!=null) {
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
}
