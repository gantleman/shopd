package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.AddressUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class AddressUserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, AddressUser> pIdx;// 一级索引

	public AddressUserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, AddressUser.class);
	}

	/**
	 * 添加一个AddressUser
	 */
	public void saveAddressUser(AddressUser addressuser) {
		pIdx.put(addressuser);
	}

	/**
	 * 根据用户Id删除一个AddressUser
	 **/
	public void removedAddressUserById(Integer addressuserId) {
		pIdx.delete(addressuserId);
	}

	/**
	 * 根据用户Id查找一个AddressUser
	 **/
	public AddressUser findAddressUserById(Integer addressuserId) {
		return pIdx.get(addressuserId);
	}

	/**
	 * 查找所有的AddressUser
	 **/
	public List<AddressUser> findAllAddressUser() {
		List<AddressUser> addressuserList = new ArrayList<AddressUser>();
		// 打开游标
		EntityCursor<AddressUser> addressuserCursorList = null;
		try {
			//获取游标
			addressuserCursorList = pIdx.entities();
			// 遍历游标
			for (AddressUser addressuser : addressuserCursorList) {
				addressuserList.add(addressuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (addressuserCursorList != null) {
				// 关闭游标
				addressuserCursorList.close();
			}
		}
		return addressuserList;
	}
	
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllAddressUserCount() {
		Long count = 0L;
		EntityCursor<AddressUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (AddressUser addressuser : cursor) {
            	if(addressuser!=null) {
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
		EntityCursor<AddressUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (AddressUser addressuser : cursor) {
            	if(addressuser!=null) {
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
