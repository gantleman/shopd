package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.CommentGoods;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class CommentGoodsDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, CommentGoods> pIdx;// 一级索引

	public CommentGoodsDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, CommentGoods.class);
	}

	/**
	 * 添加一个CommentGoods
	 */
	public void saveCommentGoods(CommentGoods commentgoods) {
		pIdx.put(commentgoods);
	}

	/**
	 * 根据用户Id删除一个CommentGoods
	 **/
	public void removedCommentGoodsById(Integer commentgoodsId) {
		pIdx.delete(commentgoodsId);
	}

	/**
	 * 根据用户Id查找一个CommentGoods
	 **/
	public CommentGoods findCommentGoodsById(Integer commentgoodsId) {
		return pIdx.get(commentgoodsId);
	}

	/**
	 * 查找所有的CommentGoods
	 **/
	public List<CommentGoods> findAllCommentGoods() {
		List<CommentGoods> commentgoodsList = new ArrayList<CommentGoods>();
		// 打开游标
		EntityCursor<CommentGoods> commentgoodsCursorList = null;
		try {
			//获取游标
			commentgoodsCursorList = pIdx.entities();
			// 遍历游标
			for (CommentGoods commentgoods : commentgoodsCursorList) {
				commentgoodsList.add(commentgoods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (commentgoodsCursorList != null) {
				// 关闭游标
				commentgoodsCursorList.close();
			}
		}
		return commentgoodsList;
	}
	
	
	/**
	 * 统计所有用户数
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
