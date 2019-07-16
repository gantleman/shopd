package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class AddressDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Address> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Address> sIdx;// 二级索引

	public AddressDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Address.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
	 * 添加一个Address
	 */
	public void saveAddress(Address address) {
		pIdx.put(address);
	}

	/**
	 * 根据用户Id删除一个Address
	 **/
	public void removedAddressById(Integer addressId) {
		pIdx.delete(addressId);
	}

	/**
	 * 根据用户名称删除Address
	 **/
	public void removedAddressByUserID(Integer userid) {
		sIdx.delete(userid);
	}

	/**
	 * 根据用户Id查找一个Address
	 **/
	public Address findAddressById(Integer addressId) {
		return pIdx.get(addressId);
	}

	/**
	 * 查找所有的Address
	 **/
	public List<Address> findAllAddress() {
		List<Address> addressList = new ArrayList<Address>();
		// 打开游标
		EntityCursor<Address> addressCursorList = null;
		try {
			//获取游标
			addressCursorList = pIdx.entities();
			// 遍历游标
			for (Address address : addressCursorList) {
				addressList.add(address);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (addressCursorList != null) {
				// 关闭游标
				addressCursorList.close();
			}
		}
		return addressList;
	}
	
	/**
	 * 根据addressName查找所有的Address
	 **/
	public List<Address> findAllAddressByUserID(Integer userid) {
	    
		List<Address> addressList=new ArrayList<Address>();
		
		EntityCursor<Address> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//遍历游标
			for (Address address : entityCursorList) {				
				addressList.add(address);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return addressList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllAddressCount() {
		Long count = 0L;
		EntityCursor<Address> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Address address : cursor) {
            	if(address!=null) {
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
		EntityCursor<Address> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Address address : cursor) {
            	if(address!=null) {
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
