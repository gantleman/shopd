package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Activity;
import java.util.List;

public interface ActivityService {

    //only read
    List<Activity> getAllActivity(Integer pageId, String url);
    
    Activity selectByKey(Integer activityid, String url);

    ///have write
    void insertActivitySelective(Activity activity);

    void deleteByActivityId(Integer activityid);

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);
}
