package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class CommentDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Comment> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Comment> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Comment> sIdx2;// 二级索引

	public CommentDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Comment.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "userid");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodsid");
	}

	/**
	 * 添加一个Comment
	 */
	public void saveComment(Comment comment) {
		pIdx.put(comment);
	}

	/**
	 * 根据用户Id删除一个Comment
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
	 * 根据用户Id查找一个Comment
	 **/
	public Comment findCommentById(Integer commentId) {
		return pIdx.get(commentId);
	}

	/**
	 * 查找所有的Comment
	 **/
	public List<Comment> findAllComment() {
		List<Comment> commentList = new ArrayList<Comment>();
		// 打开游标
		EntityCursor<Comment> commentCursorList = null;
		try {
			//获取游标
			commentCursorList = pIdx.entities();
			// 遍历游标
			for (Comment comment : commentCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (commentCursorList != null) {
				// 关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//遍历游标
			for (Comment comment : entityCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(goodsid).entities();
			//遍历游标
			for (Comment comment : entityCursorList) {
				commentList.add(comment);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return commentList;
	}

	/**
	 * 统计所有用户数
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
	 * 统计所有满足用户名的用户总数
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
	 * 统计所有满足用户名的用户总数
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
}
