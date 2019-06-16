package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class CacheDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Cache> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<String, Integer, Cache> sIdx;// 二级索引

	public CacheDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Cache.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "cName");
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
	 * 根据用户Id查找一个Cache
	 **/
	public Cache findCacheById(Integer cacheId) {
		return pIdx.get(cacheId);
	}

	/**
	 * 根据用户Id查找一个Cache
	 **/
	public Cache findCacheByName(String name) {
		return sIdx.get(name);
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
	 * 根据addressName查找所有的Address
	 **/
	public List<Cache> findAllAddressByUserID(String name) {
	    
		List<Cache> nameList=new ArrayList<Cache>();
		
		EntityCursor<Cache> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(name).entities();
			//遍历游标
			for (Cache address : entityCursorList) {
				nameList.add(address);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return nameList;
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
