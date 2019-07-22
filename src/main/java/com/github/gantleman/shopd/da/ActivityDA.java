package com.github.gantleman.shopd.da;
import java.util.ArrayList;
import java.util.List;

import com.github.gantleman.shopd.entity.Activity;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ActivityDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Activity> pIdx;// Primary Index

	public ActivityDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Activity.class);
	}

	/**
* Add a activity
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
	 * Delete one based on user ID activity
	 **/
	public boolean removedActivityById(Integer activityId) {
		return pIdx.delete(activityId);
	}

	/**
	 * Find one based on user ID activity
	 **/
	public Activity findActivityById(Integer activityId) {
		return pIdx.get(activityId);
	}

	/**
	 * Find all activity
	 **/
	public List<Activity> findAllActivity() {
		List<Activity> activityList = new ArrayList<Activity>();
		// open cursor
		EntityCursor<Activity> activityCursorList = null;
		try {
			//Get the cursor
			activityCursorList = pIdx.entities();
			// Traversal cursor
			for (Activity activity : activityCursorList) {
				activityList.add(activity);
			}
		} catch (DatabaseException e) {
		} finally {
			if (activityCursorList != null) {
				// Close the cursor
				activityCursorList.close();
			}
		}
		return activityList;
	}
	
	/**
	 * Statistics of all users
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
