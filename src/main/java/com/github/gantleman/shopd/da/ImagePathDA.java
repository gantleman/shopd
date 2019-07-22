package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ImagePath;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class ImagePathDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, ImagePath> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, ImagePath> sIdx;// Secondary index

	public ImagePathDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, ImagePath.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodid");
	}

	/**
* Add a ImagePath
	 */
	public void saveImagePath(ImagePath imagepath) {
		pIdx.put(imagepath);
	}

	/**
	 * Delete one based on user ID ImagePath
	 **/
	public void removedImagePathById(Integer imagepathId) {
		pIdx.delete(imagepathId);
	}

	/**
	 * Delete one by user nameImagePath
	 **/
	public void removedImagePathByImagePathName(Integer pathID) {
		sIdx.delete(pathID);
	}

	/**
	 * Find one based on user IDImagePath
	 **/
	public ImagePath findImagePathById(Integer pathId) {
		return pIdx.get(pathId);
	}

	/**
	 * Find all ImagePath
	 **/
	public List<ImagePath> findAllImagePath() {
		List<ImagePath> imagepathList = new ArrayList<ImagePath>();
		// open cursor
		EntityCursor<ImagePath> imagepathCursorList = null;
		try {
			//Get the cursor
			imagepathCursorList = pIdx.entities();
			// Traversal cursor
			for (ImagePath imagepath : imagepathCursorList) {
				imagepathList.add(imagepath);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (imagepathCursorList != null) {
				// Close the cursor
				imagepathCursorList.close();
			}
		}
		return imagepathList;
	}
	
	/**
	 * 根据imagepathName查找所有的ImagePath
	 **/
	public List<ImagePath> findAllImagePathByGoodID(Integer goodID) {
	    
		List<ImagePath> imagepathList=new ArrayList<ImagePath>();
		
		EntityCursor<ImagePath> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(goodID).entities();
			//Traversal cursor
			for (ImagePath imagepath : entityCursorList) {
				imagepathList.add(imagepath);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return imagepathList;
	}
	
	/**
	 * Statistics of all users
	**/
	public Long findAllImagePathCount() {
		Long count = 0L;
		EntityCursor<ImagePath> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ImagePath imagepath : cursor) {
            	if(imagepath!=null) {
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
	public Long findAllImagePathByImageGoodIDCount(Integer goodID) {
		Long count = 0L;
		EntityCursor<ImagePath> cursor = null;
        try{
            cursor = sIdx.subIndex(goodID).entities();
            for (ImagePath imagepath : cursor) {
            	if(imagepath!=null) {
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
		EntityCursor<ImagePath> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ImagePath activity : cursor) {
            	if(activity!=null) {
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
