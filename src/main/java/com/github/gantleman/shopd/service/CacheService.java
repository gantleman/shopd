package com.github.gantleman.shopd.service;

import java.util.Set;

public interface CacheService {

    public void eventAdd(String tablename);

    public void eventAdd(String tablename, Set<Integer> ID);

    public long eventCteate(String tablename);

    public long eventCteate(String tablename, Set<Integer> ID);

    public void Archive(String tablename);

    public Boolean IsCache(String tablename);

    public Boolean IsCache(String tablename, Integer ID);

    public void eventDel(String tablename, Set<Integer> ID);
}
