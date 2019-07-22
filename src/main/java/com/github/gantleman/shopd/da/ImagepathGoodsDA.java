package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ImagepathGoods;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ImagepathGoodsDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, ImagepathGoods> pIdx;// Primary Index

	public ImagepathGoodsDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, ImagepathGoods.class);
	}

	/**
* Add a ImagepathGoods
	 */
	public void saveImagepathGoods(ImagepathGoods imagepathgoods) {
		pIdx.put(imagepathgoods);
	}

	/**
	 * Delete one based on user ID ImagepathGoods
	 **/
	public void removedImagepathGoodsById(Integer imagepathgoodsId) {
		pIdx.delete(imagepathgoodsId);
	}

	/**
	 * Find one based on user IDImagepathGoods
	 **/
	public ImagepathGoods findImagepathGoodsById(Integer imagepathgoodsId) {
		return pIdx.get(imagepathgoodsId);
	}

	/**
	 * Find all ImagepathGoods
	 **/
	public List<ImagepathGoods> findAllImagepathGoods() {
		List<ImagepathGoods> imagepathgoodsList = new ArrayList<ImagepathGoods>();
		// open cursor
		EntityCursor<ImagepathGoods> imagepathgoodsCursorList = null;
		try {
			//Get the cursor
			imagepathgoodsCursorList = pIdx.entities();
			// Traversal cursor
			for (ImagepathGoods imagepathgoods : imagepathgoodsCursorList) {
				imagepathgoodsList.add(imagepathgoods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (imagepathgoodsCursorList != null) {
				// Close the cursor
				imagepathgoodsCursorList.close();
			}
		}
		return imagepathgoodsList;
	}
	
	
	/**
	 * Statistics of all users
	**/
	public Long findAllImagepathGoodsCount() {
		Long count = 0L;
		EntityCursor<ImagepathGoods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ImagepathGoods imagepathgoods : cursor) {
            	if(imagepathgoods!=null) {
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
		EntityCursor<ImagepathGoods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ImagepathGoods imagepathgoods : cursor) {
            	if(imagepathgoods!=null) {
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
