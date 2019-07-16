package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Admin;

public interface AdminService {
    ///have write
    public Admin selectByName(Admin admin);

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);
}
