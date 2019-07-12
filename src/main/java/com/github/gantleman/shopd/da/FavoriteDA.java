package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class FavoriteDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Favorite> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Favorite> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Favorite> sIdx2;// 二级索引

	public FavoriteDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Favorite.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
	 * 添加一个Favorite
	 */
	public void saveFavorite(Favorite favorite) {
		pIdx.put(favorite);
	}

	/**
	 * 根据用户Id删除一个Favorite
	 **/
	public void removedFavoriteById(Integer favoriteId) {
		pIdx.delete(favoriteId);
	}

	/**
	 * 根据用户名称删除一个Favorite
	 **/
	public void removedFavoriteByGoodsID(Integer goodsid) {
		sIdx.delete(goodsid);
	}

		/**
	 * 根据用户名称删除一个Favorite
	 **/
	public void removedFavoriteByUserID(Integer Userid) {
		sIdx2.delete(Userid);
	}

	/**
	 * 根据用户Id查找一个Favorite
	 **/
	public Favorite findFavoriteById(Integer favoriteId) {
		return pIdx.get(favoriteId);
	}

	/**
	 * 查找所有的Favorite
	 **/
	public List<Favorite> findAllFavorite() {
		List<Favorite> favoriteList = new ArrayList<Favorite>();
		// 打开游标
		EntityCursor<Favorite> favoriteCursorList = null;
		try {
			//获取游标
			favoriteCursorList = pIdx.entities();
			// 遍历游标
			for (Favorite favorite : favoriteCursorList) {
				favoriteList.add(favorite);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (favoriteCursorList != null) {
				// 关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//遍历游标
			for (Favorite favorite : entityCursorList) {
				favoriteList.add(favorite);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return favoriteList;
	}
	
	/**
	 * 统计所有用户数
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
	 * 统计所有满足用户名的用户总数
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

	public List<Favorite> findAllWhitStamp(long stamp) {
		List<Favorite> adminList = new ArrayList<Favorite>();
		// 打开游标
		EntityCursor<Favorite> adminCursorList = null;
		try {
			//获取游标
			adminCursorList = pIdx.entities();
			// 遍历游标
			for (Favorite favorite : adminCursorList) {
				if(favorite.getStamp() <= stamp) {
					adminList.add(favorite);
				}
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (adminCursorList != null) {
				// 关闭游标
				adminCursorList.close();
			}
		}
		return adminList;
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
