package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Category;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class CategoryDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Category> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<String, Integer, Category> sIdx;// Secondary index

	public CategoryDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Category.class);

		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, String.class, "catename");
	}

	/**
* Add a Category
	 */
	public void saveCategory(Category category) {
		pIdx.put(category);
	}

	/**
	 * Delete one based on user ID Category
	 **/
	public void removedCategoryById(Integer categoryId) {
		pIdx.delete(categoryId);
	}

	/**
	 * Find one based on user IDCategory
	 **/
	public Category findCategoryById(Integer categoryId) {
		return pIdx.get(categoryId);
	}

	/**
	 * 根据chatName查找所有的Chat
	 **/
	public List<Category> findAllChatByCategoryName(String categoryname) {
	    
		List<Category> categoryList=new ArrayList<Category>();
		
		EntityCursor<Category> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(categoryname).entities();
			//Traversal cursor
			for (Category category : entityCursorList) {
				categoryList.add(category);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return categoryList;
	}

	/**
	 * Find all Category
	 **/
	public List<Category> findAllCategory() {
		List<Category> categoryList = new ArrayList<Category>();
		// open cursor
		EntityCursor<Category> categoryCursorList = null;
		try {
			//Get the cursor
			categoryCursorList = pIdx.entities();
			// Traversal cursor
			for (Category category : categoryCursorList) {
				categoryList.add(category);
			}
		} catch (DatabaseException e) {
		} finally {
			if (categoryCursorList != null) {
				// Close the cursor
				categoryCursorList.close();
			}
		}
		return categoryList;
	}
	
	/**
	 * Statistics of all users
	**/
	public Long findAllCategoryCount() {
		Long count = 0L;
		EntityCursor<Category> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Category category : cursor) {
            	if(category!=null) {
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
		EntityCursor<Category> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Category category : cursor) {
            	if(category!=null) {
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
