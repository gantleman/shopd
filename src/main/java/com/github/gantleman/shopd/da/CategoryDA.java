package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class CategoryDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Category> pIdx;// 一级索引

	public CategoryDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Category.class);
	}

	/**
	 * 添加一个Category
	 */
	public void saveCategory(Category category) {
		pIdx.put(category);
	}

	/**
	 * 根据用户Id删除一个Category
	 **/
	public void removedCategoryById(Integer categoryId) {
		pIdx.delete(categoryId);
	}

	/**
	 * 根据用户Id查找一个Category
	 **/
	public Category findCategoryById(Integer categoryId) {
		return pIdx.get(categoryId);
	}

	/**
	 * 查找所有的Category
	 **/
	public List<Category> findAllCategory() {
		List<Category> categoryList = new ArrayList<Category>();
		// 打开游标
		EntityCursor<Category> categoryCursorList = null;
		try {
			//获取游标
			categoryCursorList = pIdx.entities();
			// 遍历游标
			for (Category category : categoryCursorList) {
				categoryList.add(category);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (categoryCursorList != null) {
				// 关闭游标
				categoryCursorList.close();
			}
		}
		return categoryList;
	}
	
	/**
	 * 统计所有用户数
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
}
