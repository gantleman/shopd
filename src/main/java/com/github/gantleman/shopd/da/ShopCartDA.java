package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ShopCart;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class ShopCartDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, ShopCart> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, ShopCart> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, ShopCart> sIdx2;// 二级索引

	public ShopCartDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, ShopCart.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
	 * 添加一个ShopCart
	 */
	public void saveShopCart(ShopCart shopcart) {
		pIdx.put(shopcart);
	}

	/**
	 * 根据用户Id删除一个ShopCart
	 **/
	public void removedShopCartById(Integer shopcartId) {
		pIdx.delete(shopcartId);
	}

	/**
	 * 根据用户名称删除一个ShopCart
	 **/
	public void removedShopCartByGoodsID(Integer goodsid) {
		sIdx.delete(goodsid);
	}

	/**
	 * 根据用户Id查找一个ShopCart
	 **/
	public ShopCart findShopCartById(Integer shopcartId) {
		return pIdx.get(shopcartId);
	}

	/**
	 * 查找所有的ShopCart
	 **/
	public List<ShopCart> findAllShopCart() {
		List<ShopCart> shopcartList = new ArrayList<ShopCart>();
		// 打开游标
		EntityCursor<ShopCart> shopcartCursorList = null;
		try {
			//获取游标
			shopcartCursorList = pIdx.entities();
			// 遍历游标
			for (ShopCart shopcart : shopcartCursorList) {
				shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (shopcartCursorList != null) {
				// 关闭游标
				shopcartCursorList.close();
			}
		}
		return shopcartList;
	}
	
	/**
	 * 根据shopcartName查找所有的ShopCart
	 **/
	public List<ShopCart> findAllShopCartByGoodsID(Integer goodsid) {
	    
		List<ShopCart> shopcartList=new ArrayList<ShopCart>();
		
		EntityCursor<ShopCart> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(goodsid).entities();
			//遍历游标
			for (ShopCart shopcart : entityCursorList) {
				shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return shopcartList;
	}

	/**
	 * 根据shopcartName查找所有的ShopCart
	 **/
	public List<ShopCart> findAllShopCartByUGID(Integer userid, Integer goodsid) {
	    
		List<ShopCart> shopcartList=new ArrayList<ShopCart>();
		
		EntityCursor<ShopCart> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//遍历游标
			for (ShopCart shopcart : entityCursorList) {
				if(shopcart.getGoodsid() == goodsid)
					shopcartList.add(shopcart);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return shopcartList;
	}

	/**
	 * 统计所有用户数
	**/
	public Long findAllShopCartCount() {
		Long count = 0L;
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopCart shopcart : cursor) {
            	if(shopcart!=null) {
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
	public Long findAllShopCartByGoodsIDCount(Integer goodsid) {
		Long count = 0L;
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = sIdx.subIndex(goodsid).entities();
            for (ShopCart shopcart : cursor) {
            	if(shopcart!=null) {
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
		EntityCursor<ShopCart> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ShopCart activity : cursor) {
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
