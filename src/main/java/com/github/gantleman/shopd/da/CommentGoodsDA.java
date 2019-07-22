package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.CommentGoods;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class CommentGoodsDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, CommentGoods> pIdx;// Primary Index

	public CommentGoodsDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, CommentGoods.class);
	}

	/**
* Add a CommentGoods
	 */
	public void saveCommentGoods(CommentGoods commentgoods) {
		pIdx.put(commentgoods);
	}

	/**
	 * Delete one based on user ID CommentGoods
	 **/
	public void removedCommentGoodsById(Integer commentgoodsId) {
		pIdx.delete(commentgoodsId);
	}

	/**
	 * Find one based on user IDCommentGoods
	 **/
	public CommentGoods findCommentGoodsById(Integer commentgoodsId) {
		return pIdx.get(commentgoodsId);
	}

	/**
	 * Find all CommentGoods
	 **/
	public List<CommentGoods> findAllCommentGoods() {
		List<CommentGoods> commentgoodsList = new ArrayList<CommentGoods>();
		// open cursor
		EntityCursor<CommentGoods> commentgoodsCursorList = null;
		try {
			//Get the cursor
			commentgoodsCursorList = pIdx.entities();
			// Traversal cursor
			for (CommentGoods commentgoods : commentgoodsCursorList) {
				commentgoodsList.add(commentgoods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (commentgoodsCursorList != null) {
				// Close the cursor
				commentgoodsCursorList.close();
			}
		}
		return commentgoodsList;
	}
	
	
	/**
	 * Statistics of all users
	**/
	public Long findAllCommentGoodsCount() {
		Long count = 0L;
		EntityCursor<CommentGoods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (CommentGoods commentgoods : cursor) {
            	if(commentgoods!=null) {
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
		EntityCursor<CommentGoods> cursor = null;
        try{
            cursor = pIdx.entities();
            for (CommentGoods commentgoods : cursor) {
            	if(commentgoods!=null) {
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
