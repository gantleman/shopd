package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.*;

public interface CacheService {
    public void eventAdd(String tablename);

    public void timeTask(String tablename);
}
