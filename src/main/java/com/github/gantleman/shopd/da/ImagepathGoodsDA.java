package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ImagepathGoods;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ImagepathGoodsDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, ImagepathGoods> pIdx;// 一级索引

	public ImagepathGoodsDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, ImagepathGoods.class);
	}

	/**
	 * 添加一个ImagepathGoods
	 */
	public void saveImagepathGoods(ImagepathGoods imagepathgoods) {
		pIdx.put(imagepathgoods);
	}

	/**
	 * 根据用户Id删除一个ImagepathGoods
	 **/
	public void removedImagepathGoodsById(Integer imagepathgoodsId) {
		pIdx.delete(imagepathgoodsId);
	}

	/**
	 * 根据用户Id查找一个ImagepathGoods
	 **/
	public ImagepathGoods findImagepathGoodsById(Integer imagepathgoodsId) {
		return pIdx.get(imagepathgoodsId);
	}

	/**
	 * 查找所有的ImagepathGoods
	 **/
	public List<ImagepathGoods> findAllImagepathGoods() {
		List<ImagepathGoods> imagepathgoodsList = new ArrayList<ImagepathGoods>();
		// 打开游标
		EntityCursor<ImagepathGoods> imagepathgoodsCursorList = null;
		try {
			//获取游标
			imagepathgoodsCursorList = pIdx.entities();
			// 遍历游标
			for (ImagepathGoods imagepathgoods : imagepathgoodsCursorList) {
				imagepathgoodsList.add(imagepathgoods);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (imagepathgoodsCursorList != null) {
				// 关闭游标
				imagepathgoodsCursorList.close();
			}
		}
		return imagepathgoodsList;
	}
	
	
	/**
	 * 统计所有用户数
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
