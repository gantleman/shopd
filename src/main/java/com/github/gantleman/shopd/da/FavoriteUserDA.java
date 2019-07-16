package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.FavoriteUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class FavoriteUserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, FavoriteUser> pIdx;// 一级索引

	public FavoriteUserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, FavoriteUser.class);
	}

	/**
	 * 添加一个FavoriteUser
	 */
	public void saveFavoriteUser(FavoriteUser favoriteuser) {
		pIdx.put(favoriteuser);
	}

	/**
	 * 根据用户Id删除一个FavoriteUser
	 **/
	public void removedFavoriteUserById(Integer favoriteuserId) {
		pIdx.delete(favoriteuserId);
	}

	/**
	 * 根据用户Id查找一个FavoriteUser
	 **/
	public FavoriteUser findFavoriteUserById(Integer favoriteuserId) {
		return pIdx.get(favoriteuserId);
	}

	/**
	 * 查找所有的FavoriteUser
	 **/
	public List<FavoriteUser> findAllFavoriteUser() {
		List<FavoriteUser> favoriteuserList = new ArrayList<FavoriteUser>();
		// 打开游标
		EntityCursor<FavoriteUser> favoriteuserCursorList = null;
		try {
			//获取游标
			favoriteuserCursorList = pIdx.entities();
			// 遍历游标
			for (FavoriteUser favoriteuser : favoriteuserCursorList) {
				favoriteuserList.add(favoriteuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (favoriteuserCursorList != null) {
				// 关闭游标
				favoriteuserCursorList.close();
			}
		}
		return favoriteuserList;
	}
	
	
	/**
	 * 统计所有用户数
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
