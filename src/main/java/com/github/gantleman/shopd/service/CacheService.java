package com.github.gantleman.shopd.service;

public interface CacheService {
    public void eventAdd(String tablename);

    public long eventCteate(String tablename);

    public void Archive(String tablename);
}
