package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ImagePath;
import java.util.List;

public interface ImagePathService {

    //only read
    public ImagePath getImagepathByKey(Integer imagePathid, String url);
    
    public List<ImagePath> findImagePath(Integer goodsid, String url);

    //have write
    public void insertImagePath(ImagePath imagePath);

    public void TickBack();

    public void TickBack_extra();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshUserDBD(Integer pageID, boolean andAll, boolean refresRedis);
}
