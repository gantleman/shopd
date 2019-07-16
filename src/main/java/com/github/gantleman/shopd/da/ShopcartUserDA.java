package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ShopcartUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ShopcartUserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, ShopcartUser> pIdx;// 一级索引

	public ShopcartUserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, ShopcartUser.class);
	}

	/**
	 * 添加一个ShopcartUser
	 */
	public void saveShopcartUser(ShopcartUser shopcartuser) {
		pIdx.put(shopcartuser);
	}

	/**
	 * 根据用户Id删除一个ShopcartUser
	 **/
	public void removedShopcartUserById(Integer shopcartuserId) {
		pIdx.delete(shopcartuserId);
	}

	/**
	 * 根据用户Id查找一个ShopcartUser
	 **/
	public ShopcartUser findShopcartUserById(Integer shopcartuserId) {
		return pIdx.get(shopcartuserId);
	}

	/**
	 * 查找所有的ShopcartUser
	 **/
	public List<ShopcartUser> findAllShopcartUser() {
		List<ShopcartUser> shopcartuserList = new ArrayList<ShopcartUser>();
		// 打开游标
		EntityCursor<ShopcartUser> shopcartuserCursorList = null;
		try {
			//获取游标
			shopcartuserCursorList = pIdx.entities();
			// 遍历游标
			for (ShopcartUser shopcartuser : shopcartuserCursorList) {
				shopcartuserList.add(shopcartuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (shopcartuserCursorList != null) {
				// 关闭游标
				shopcartuserCursorList.close();
			}
		}
		return shopcartuserList;
	}
	
	
	/**
	 * 统计所有用户数
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
