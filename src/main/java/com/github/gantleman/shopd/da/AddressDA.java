package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Address;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class AddressDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Address> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Address> sIdx;// Secondary index

	public AddressDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Address.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
	}

	/**
* Add a Address
	 */
	public void saveAddress(Address address) {
		pIdx.put(address);
	}

	/**
	 * Delete one based on user ID Address
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
	 * Find one based on user IDAddress
	 **/
	public Address findAddressById(Integer addressId) {
		return pIdx.get(addressId);
	}

	/**
	 * Find all Address
	 **/
	public List<Address> findAllAddress() {
		List<Address> addressList = new ArrayList<Address>();
		// open cursor
		EntityCursor<Address> addressCursorList = null;
		try {
			//Get the cursor
			addressCursorList = pIdx.entities();
			// Traversal cursor
			for (Address address : addressCursorList) {
				addressList.add(address);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (addressCursorList != null) {
				// Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//Traversal cursor
			for (Address address : entityCursorList) {				
				addressList.add(address);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return addressList;
	}
	
	/**
	 * Statistics of all users
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
