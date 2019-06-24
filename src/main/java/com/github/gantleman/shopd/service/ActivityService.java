package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.*;
import java.util.List;

public interface ActivityService {

    //only read
    List<Activity> getAllActivity();
    
    Activity selectByKey(Integer activityid);

    ///have write
    void insertActivitySelective(Activity activity);

    void deleteByActivityId(Integer activityid);
}
