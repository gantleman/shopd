package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.AddressUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class AddressUserDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, AddressUser> pIdx;// Primary Index

	public AddressUserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, AddressUser.class);
	}

	/**
* Add a AddressUser
	 */
	public void saveAddressUser(AddressUser addressuser) {
		pIdx.put(addressuser);
	}

	/**
	 * Delete one based on user ID AddressUser
	 **/
	public void removedAddressUserById(Integer addressuserId) {
		pIdx.delete(addressuserId);
	}

	/**
	 * Find one based on user IDAddressUser
	 **/
	public AddressUser findAddressUserById(Integer addressuserId) {
		return pIdx.get(addressuserId);
	}

	/**
	 * Find all AddressUser
	 **/
	public List<AddressUser> findAllAddressUser() {
		List<AddressUser> addressuserList = new ArrayList<AddressUser>();
		// open cursor
		EntityCursor<AddressUser> addressuserCursorList = null;
		try {
			//Get the cursor
			addressuserCursorList = pIdx.entities();
			// Traversal cursor
			for (AddressUser addressuser : addressuserCursorList) {
				addressuserList.add(addressuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (addressuserCursorList != null) {
				// Close the cursor
				addressuserCursorList.close();
			}
		}
		return addressuserList;
	}
	
	
	/**
	 * Statistics of all users
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
