package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class AdminDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Admin> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<String, Integer, Admin> sIdx;// 二级索引

	public AdminDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Admin.class);

		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "adminname");
	}

	/**
	 * 添加一个Admin
	 */
	public void saveAdmin(Admin admin) {
		admin.MakeStamp();
		pIdx.put(admin);
	}

	/**
	 * 根据用户Id删除一个Admin
	 **/
	public void removedAdminById(Integer adminId) {
		pIdx.delete(adminId);
	}

	/**
	 * 根据用户名称删除Chat
	 **/
	public void removedChatBySendUserID(String adminname) {
		sIdx.delete(adminname);
	}

	/**
	 * 根据用户Id查找一个Admin
	 **/
	public Admin findAdminById(Integer adminId) {
		return pIdx.get(adminId);
	}

	/**
	 * 查找所有的Admin
	 **/
	public List<Admin> findAllAdmin() {
		List<Admin> adminList = new ArrayList<Admin>();
		// 打开游标
		EntityCursor<Admin> adminCursorList = null;
		try {
			//获取游标
			adminCursorList = pIdx.entities();
			// 遍历游标
			for (Admin admin : adminCursorList) {
				adminList.add(admin);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (adminCursorList != null) {
				// 关闭游标
				adminCursorList.close();
			}
		}
		return adminList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllAdminCount() {
		Long count = 0L;
		EntityCursor<Admin> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Admin admin : cursor) {
            	if(admin!=null) {
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
	 * 根据chatName查找所有的Chat
	 **/
	public List<Admin> findAllChatByAdminNameAndPassword(String adminname, String passwrod) {
	    
		List<Admin> adminList=new ArrayList<Admin>();
		
		EntityCursor<Admin> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(adminname).entities();
			//遍历游标
			for (Admin admin : entityCursorList) {

				if( admin.getPassword().equals(passwrod)) {
					adminList.add(admin);
				}
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
		return adminList;
	}


	public List<Admin> findAllUserWhitStamp(long stamp) {
		List<Admin> adminList = new ArrayList<Admin>();
		// 打开游标
		EntityCursor<Admin> adminCursorList = null;
		try {
			//获取游标
			adminCursorList = pIdx.entities();
			// 遍历游标
			for (Admin admin : adminCursorList) {
				if(admin.getStamp() <= stamp) {
					adminList.add(admin);
				}
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (adminCursorList != null) {
				// 关闭游标
				adminCursorList.close();
			}
		}
		return adminList;
	}

	public Integer IsEmpty() {
		Integer count = 1;
		EntityCursor<Admin> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Admin admin : cursor) {
            	if(admin!=null) {
					count = 0;
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
