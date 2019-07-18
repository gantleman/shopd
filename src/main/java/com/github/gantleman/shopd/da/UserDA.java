package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.User;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class UserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, User> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<String, Integer, User> sIdx;// 二级索引

	public UserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, User.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "username");
	}

	/**
	 * 添加一个User
	 */
	public void saveUser(User user) {
		pIdx.put(user);
	}

	/**
	 * 根据用户Id删除一个User
	 **/
	public void removedUserById(Integer userId) {
		pIdx.delete(userId);
	}

	/**
	 * 根据用户名称删除一个User
	 **/
	public void removedUserByUserName(String userName) {
		sIdx.delete(userName);
	}
	
	/**
	 *  根据用户Id修改单个用户
	 **/
	public User modifyUserById(User user) {
		
		User modifyUser=null;
		
		modifyUser=pIdx.get(user.getUserid());
		modifyUser.setUsername(user.getUsername());
		modifyUser.setPassword(user.getPassword());
		
		return modifyUser;
	}

	/**
	 * 根据用户Id查找一个User
	 **/
	public User findUserById(Integer userId) {
		return pIdx.get(userId);
	}

	/**
	 * 查找所有的User
	 **/
	public List<User> findAllUser() {
		List<User> userList = new ArrayList<User>();
		// 打开游标
		EntityCursor<User> userCursorList = null;
		try {
			//获取游标
			userCursorList = pIdx.entities();
			// 遍历游标
			for (User user : userCursorList) {
				userList.add(user);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (userCursorList != null) {
				// 关闭游标
				userCursorList.close();
			}
		}
		return userList;
	}
	
	/**
	 * 根据userName查找所有的User
	 **/
	public List<User> findAllUserByUserName(String userName) {
	    
		List<User> userList=new ArrayList<User>();
		
		EntityCursor<User> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userName).entities();
			//遍历游标
			for (User user : entityCursorList) {
				userList.add(user);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return userList;
	}

		/**
	 * 根据userName查找所有的User
	 **/
	public List<User> findAllUserByUserNameAndPassword(String userName, String passWord) {
	    
		List<User> userList=new ArrayList<User>();
		
		EntityCursor<User> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userName).entities();
			//遍历游标
			for (User user : entityCursorList) {
				if(user.getPassword().equals(passWord))
					userList.add(user);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return userList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllUserCount() {
		Long count = 0L;
		EntityCursor<User> cursor = null;
        try{
            cursor = pIdx.entities();
            for (User user : cursor) {
            	if(user!=null) {
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
	public Long findAllUserByUserNameCount(String userName) {
		Long count = 0L;
		EntityCursor<User> cursor = null;
        try{
            cursor = sIdx.subIndex(userName).entities();
            for (User user : cursor) {
            	if(user!=null) {
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
		EntityCursor<User> cursor = null;
        try{
            cursor = pIdx.entities();
            for (User user : cursor) {
            	if(user!=null) {
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
