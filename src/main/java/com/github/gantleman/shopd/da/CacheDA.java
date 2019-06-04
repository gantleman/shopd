package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;


public class CacheDA {

	// 主键字段类型,实体类
	PrimaryIndex<String, Cache> pIdx;// 一级索引

	public CacheDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(String.class, Cache.class);
	}

	/**
	 * 添加一个Cache
	 */
	public void saveCache(Cache cache) {
		pIdx.put(cache);
	}

	/**
	 * 根据用户Id删除一个Cache
	 **/
	public void removedCacheById(String cacheId) {
		pIdx.delete(cacheId);
	}

	/**
	 * 根据用户Id查找一个Cache
	 **/
	public Cache findCacheById(String cacheId) {
		return pIdx.get(cacheId);
	}

	/**
	 * 查找所有的Cache
	 **/
	public List<Cache> findAllCache() {
		List<Cache> cacheList = new ArrayList<Cache>();
		// 打开游标
		EntityCursor<Cache> cacheCursorList = null;
		try {
			//获取游标
			cacheCursorList = pIdx.entities();
			// 遍历游标
			for (Cache cache : cacheCursorList) {
				cacheList.add(cache);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (cacheCursorList != null) {
				// 关闭游标
				cacheCursorList.close();
			}
		}
		return cacheList;
	}
		
	/**
	 * 统计所有用户数
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
