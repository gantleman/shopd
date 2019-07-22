package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Admin;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class AdminDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Admin> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<String, Integer, Admin> sIdx;// Secondary index

	public AdminDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Admin.class);

		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "adminname");
	}

	/**
* Add a Admin
	 */
	public void saveAdmin(Admin admin) {
		pIdx.put(admin);
	}

	/**
	 * Delete one based on user ID Admin
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
	 * Find one based on user IDAdmin
	 **/
	public Admin findAdminById(Integer adminId) {
		return pIdx.get(adminId);
	}

	/**
	 * Find all Admin
	 **/
	public List<Admin> findAllAdmin() {
		List<Admin> adminList = new ArrayList<Admin>();
		// open cursor
		EntityCursor<Admin> adminCursorList = null;
		try {
			//Get the cursor
			adminCursorList = pIdx.entities();
			// Traversal cursor
			for (Admin admin : adminCursorList) {
				adminList.add(admin);
			}
		} catch (DatabaseException e) {
		} finally {
			if (adminCursorList != null) {
				// Close the cursor
				adminCursorList.close();
			}
		}
		return adminList;
	}
	
	/**
	 * Statistics of all users
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(adminname).entities();
			//Traversal cursor
			for (Admin admin : entityCursorList) {

				if( admin.getPassword().equals(passwrod)) {
					adminList.add(admin);
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return adminList;
	}
	public boolean IsEmpty() {
		boolean count = true;
		EntityCursor<Admin> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Admin admin : cursor) {
            	if(admin!=null) {
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
