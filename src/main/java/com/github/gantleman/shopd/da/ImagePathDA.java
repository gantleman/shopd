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

	// 主键字段类型,实体类
	PrimaryIndex<Integer, ImagePath> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, ImagePath> sIdx;// 二级索引

	public ImagePathDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, ImagePath.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "goodid");
	}

	/**
	 * 添加一个ImagePath
	 */
	public void saveImagePath(ImagePath imagepath) {
		pIdx.put(imagepath);
	}

	/**
	 * 根据用户Id删除一个ImagePath
	 **/
	public void removedImagePathById(Integer imagepathId) {
		pIdx.delete(imagepathId);
	}

	/**
	 * 根据用户名称删除一个ImagePath
	 **/
	public void removedImagePathByImagePathName(Integer pathID) {
		sIdx.delete(pathID);
	}

	/**
	 * 根据用户Id查找一个ImagePath
	 **/
	public ImagePath findImagePathById(Integer pathId) {
		return pIdx.get(pathId);
	}

	/**
	 * 查找所有的ImagePath
	 **/
	public List<ImagePath> findAllImagePath() {
		List<ImagePath> imagepathList = new ArrayList<ImagePath>();
		// 打开游标
		EntityCursor<ImagePath> imagepathCursorList = null;
		try {
			//获取游标
			imagepathCursorList = pIdx.entities();
			// 遍历游标
			for (ImagePath imagepath : imagepathCursorList) {
				imagepathList.add(imagepath);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (imagepathCursorList != null) {
				// 关闭游标
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
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(goodID).entities();
			//遍历游标
			for (ImagePath imagepath : entityCursorList) {
				imagepathList.add(imagepath);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return imagepathList;
	}
	
	/**
	 * 统计所有用户数
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
	 * 统计所有满足用户名的用户总数
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
