package com.github.gantleman.shopd.service;

import java.util.List;

public interface CacheService {

    public static final int STATUS_INSERT = 1;

    public static final int STATUS_DELETE= 2;

    public static final int STATUS_UPDATE = 3;

    public long eventCteate(String tablename);

    public void Archive(String tablename);

    public Boolean IsCache(String tablename, Integer pageID);

    public Boolean IsCache(String tablename);

    public List<Integer> PageOut(String tablename);

    public Integer PageBegin(Integer pageID);

    public Integer PageEnd(Integer pageID);

    public Integer PageID(Integer ID);

    public boolean IsLocal(String url);

    public void RemoteRefresh(String url, Integer Id);

    public void RemoteRefresh(String url, String name);
}
