package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import com.github.gantleman.shopd.entity.Activity;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ActivityDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Activity> pIdx;// 一级索引

	public ActivityDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Activity.class);
	}

	/**
	 * 添加一个activity
	 */
	public void saveActivity(Activity activity) {
		pIdx.put(activity);
	}

	public void saveListActivity(List<Activity> activity) {
		for (Activity ac : activity) {
			pIdx.put(ac);
		}
	}

	/**
	 * 根据用户Id删除一个activity
	 **/
	public boolean removedActivityById(Integer activityId) {
		return pIdx.delete(activityId);
	}

	/**
	 * 根据用户Id查找一个activity
	 **/
	public Activity findActivityById(Integer activityId) {
		return pIdx.get(activityId);
	}

	/**
	 * 查找所有的activity
	 **/
	public List<Activity> findAllActivity() {
		List<Activity> activityList = new ArrayList<Activity>();
		// 打开游标
		EntityCursor<Activity> activityCursorList = null;
		try {
			//获取游标
			activityCursorList = pIdx.entities();
			// 遍历游标
			for (Activity activity : activityCursorList) {
				activityList.add(activity);
			}
		} catch (DatabaseException e) {
		} finally {
			if (activityCursorList != null) {
				// 关闭游标
				activityCursorList.close();
			}
		}
		return activityList;
	}
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllActivityCount() {
		Long count = 0L;
		EntityCursor<Activity> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Activity activity : cursor) {
            	if(activity!=null) {
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
		EntityCursor<Activity> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Activity activity : cursor) {
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
