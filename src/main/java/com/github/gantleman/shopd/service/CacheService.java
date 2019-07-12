package com.github.gantleman.shopd.service;

import java.util.List;

public interface CacheService {

    public long eventCteate(String tablename);

    public void Archive(String tablename);

    public Boolean IsCache(String tablename, Integer pageID);

    public Boolean IsCache(String tablename);

    public List<Integer> PageOut(String tablename);

    public Integer PageBegin(Integer pageID);

    public Integer PageEnd(Integer pageID);

    public Integer PageID(Integer ID);
}
