package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Comment;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class CommentDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Comment> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Comment> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Comment> sIdx2;// Secondary index

	public CommentDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Comment.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
	}

	/**
* Add a Comment
	 */
	public void saveComment(Comment comment) {
		pIdx.put(comment);
	}

	/**
	 * Delete one based on user ID Comment
	 **/
	public void removedCommentById(Integer commentId) {
		pIdx.delete(commentId);
	}

	/**
	 * 根据userid删除一个Comment
	 **/
	public void removedCommentByUserID(Integer userid) {
		sIdx.delete(userid);
	}

	/**
	 * 根据goodsid删除一个Comment
	 **/
	public void removedCommentByGoodsID(Integer goodsid) {
		sIdx2.delete(goodsid);
	}
	
	/**
	 * Find one based on user IDComment
	 **/
	public Comment findCommentById(Integer commentId) {
		return pIdx.get(commentId);
	}

	/**
	 * Find all Comment
	 **/
	public List<Comment> findAllComment() {
		List<Comment> commentList = new ArrayList<Comment>();
		// open cursor
		EntityCursor<Comment> commentCursorList = null;
		try {
			//Get the cursor
			commentCursorList = pIdx.entities();
			// Traversal cursor
			for (Comment comment : commentCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (commentCursorList != null) {
				// Close the cursor
				commentCursorList.close();
			}
		}
		return commentList;
	}
	
	/**
	 * 根据commentName查找所有的Comment
	 **/
	public List<Comment> findAllCommentByUserID(Integer userid) {
	    
		List<Comment> commentList=new ArrayList<Comment>();
		
		EntityCursor<Comment> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//Traversal cursor
			for (Comment comment : entityCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return commentList;
	}
	
		/**
	 * 根据commentName查找所有的Comment
	 **/
	public List<Comment> findAllCommentByGoodsID(Integer goodsid) {
	    
		List<Comment> commentList=new ArrayList<Comment>();
		
		EntityCursor<Comment> entityCursorList=null;
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(goodsid).entities();
			//Traversal cursor
			for (Comment comment : entityCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return commentList;
	}

	/**
	 * Statistics of all users
	**/
	public Long findAllCommentCount() {
		Long count = 0L;
		EntityCursor<Comment> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Comment comment : cursor) {
            	if(comment!=null) {
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
	public Long findAllCommentByByUserIDCount(Integer userid) {
		Long count = 0L;
		EntityCursor<Comment> cursor = null;
        try{
            cursor = sIdx.subIndex(userid).entities();
            for (Comment comment : cursor) {
            	if(comment!=null) {
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
	public Long findAllCommentByByGoodsIDCount(Integer goodsid) {
		Long count = 0L;
		EntityCursor<Comment> cursor = null;
        try{
            cursor = sIdx2.subIndex(goodsid).entities();
            for (Comment comment : cursor) {
            	if(comment!=null) {
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
		EntityCursor<Comment> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Comment activity : cursor) {
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
