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

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Goods> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Goods> sIdx;// 二级索引
	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<String, Integer, Goods> sIdx2;// 二级索引

	public GoodsDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Goods.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "category");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, String.class, "goodsname");
	}

	/**
	 * 添加一个Goods
	 */
	public void saveGoods(Goods goods) {
		pIdx.put(goods);
	}

	/**
	 * 根据用户Id删除一个Goods
	 **/
	public void removedGoodsById(Integer goodsId) {
		pIdx.delete(goodsId);
	}

	/**
	 * 根据用户名称删除一个Goods
	 **/
	public void removedGoodsByActivityID(Integer activityid) {
		sIdx.delete(activityid);
	}
	
	/**
	 * 根据用户名称删除一个Goods
	 **/
	public void removedGoodsByCategory(Integer category) {
		sIdx.delete(category);
	}

	/**
	 * 根据用户Id查找一个Goods
	 **/
	public Goods findGoodsById(Integer goodsId) {
		return pIdx.get(goodsId);
	}

	/**
	 * 查找所有的Goods
	 **/
	public List<Goods> findAllGoods() {
		List<Goods> goodsList = new ArrayList<Goods>();
		// 打开游标
		EntityCursor<Goods> goodsCursorList = null;
		try {
			//获取游标
			goodsCursorList = pIdx.entities();
			// 遍历游标
			for (Goods goods : goodsCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (goodsCursorList != null) {
				// 关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(category).entities();
			//遍历游标
			for (Goods goods : entityCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(name).entities();
			//遍历游标
			for (Goods goods : entityCursorList) {
				goodsList.add(goods);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return goodsList;
	}

	/**
	 * 统计所有用户数
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
	 * 统计所有满足用户名的用户总数
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
