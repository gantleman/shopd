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

	// Primary key field type, entity class
	PrimaryIndex<Integer, User> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<String, Integer, User> sIdx;// Secondary index

	public UserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, User.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "username");
	}

	/**
* Add a User
	 */
	public void saveUser(User user) {
		pIdx.put(user);
	}

	/**
	 * Delete one based on user ID User
	 **/
	public void removedUserById(Integer userId) {
		pIdx.delete(userId);
	}

	/**
	 * Delete one by user name User
	 **/
	public void removedUserByUserName(String userName) {
		sIdx.delete(userName);
	}
	
	/**
	 *  Modify a single user according to user ID
	 **/
	public User modifyUserById(User user) {
		
		User modifyUser=null;
		
		modifyUser=pIdx.get(user.getUserid());
		modifyUser.setUsername(user.getUsername());
		modifyUser.setPassword(user.getPassword());
		
		return modifyUser;
	}

	/**
	 * Find one based on user IDUser
	 **/
	public User findUserById(Integer userId) {
		return pIdx.get(userId);
	}

	/**
	 * Find all User
	 **/
	public List<User> findAllUser() {
		List<User> userList = new ArrayList<User>();
		// open cursor
		EntityCursor<User> userCursorList = null;
		try {
			//Get the cursor
			userCursorList = pIdx.entities();
			// Traversal cursor
			for (User user : userCursorList) {
				userList.add(user);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (userCursorList != null) {
				// Close the cursor
				userCursorList.close();
			}
		}
		return userList;
	}
	
	/**
	 * Find all Users based on userName
	 **/
	public List<User> findAllUserByUserName(String userName) {
	    
		List<User> userList=new ArrayList<User>();
		
		EntityCursor<User> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userName).entities();
			//Traversal cursor
			for (User user : entityCursorList) {
				userList.add(user);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return userList;
	}

	/**
	 *Find all Users based on userName
	 **/
	public List<User> findAllUserByUserNameAndPassword(String userName, String passWord) {
	    
		List<User> userList=new ArrayList<User>();
		
		EntityCursor<User> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userName).entities();
			//Traversal cursor
			for (User user : entityCursorList) {
				if(user.getPassword().equals(passWord))
					userList.add(user);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return userList;
	}
	
	/**
	 * Statistics of all users
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
	 *Statistics the total number of users who satisfy the username
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
