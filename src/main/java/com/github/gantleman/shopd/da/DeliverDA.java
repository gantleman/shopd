package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class DeliverDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Deliver> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Deliver> sIdx;// 二级索引

	public DeliverDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Deliver.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "orderid");
	}

	/**
	 * 添加一个Deliver
	 */
	public void saveDeliver(Deliver deliver) {
		pIdx.put(deliver);
	}

	/**
	 * 根据用户Id删除一个Deliver
	 **/
	public void removedDeliverById(Integer deliverId) {
		pIdx.delete(deliverId);
	}

	/**
	 * 根据用户名称删除一个Deliver
	 **/
	public void removedDeliverByOrderID(Integer orderid) {
		sIdx.delete(orderid);
	}

	/**
	 * 根据用户Id查找一个Deliver
	 **/
	public Deliver findDeliverById(Integer deliverId) {
		return pIdx.get(deliverId);
	}

	/**
	 * 查找所有的Deliver
	 **/
	public List<Deliver> findAllDeliver() {
		List<Deliver> deliverList = new ArrayList<Deliver>();
		// 打开游标
		EntityCursor<Deliver> deliverCursorList = null;
		try {
			//获取游标
			deliverCursorList = pIdx.entities();
			// 遍历游标
			for (Deliver deliver : deliverCursorList) {
				deliverList.add(deliver);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (deliverCursorList != null) {
				// 关闭游标
				deliverCursorList.close();
			}
		}
		return deliverList;
	}
	
	/**
	 * 根据deliverName查找所有的Deliver
	 **/
	public List<Deliver> findAllDeliverByOrderID(Integer orderid) {
	    
		List<Deliver> deliverList=new ArrayList<Deliver>();
		
		EntityCursor<Deliver> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(orderid).entities();
			//遍历游标
			for (Deliver deliver : entityCursorList) {
				deliverList.add(deliver);
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
		return deliverList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllDeliverCount() {
		Long count = 0L;
		EntityCursor<Deliver> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Deliver deliver : cursor) {
            	if(deliver!=null) {
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
	public Long findAllDeliverByOrderIDCount(Integer orderid) {
		Long count = 0L;
		EntityCursor<Deliver> cursor = null;
        try{
            cursor = sIdx.subIndex(orderid).entities();
            for (Deliver deliver : cursor) {
            	if(deliver!=null) {
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
