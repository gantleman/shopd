package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ImagePath;
import java.util.List;

public interface ImagePathService {

    //only read
    public List<ImagePath> findImagePath(Integer goodsid);

    //have write
    public void insertImagePath(ImagePath imagePath);  

    public void TickBack();

    public void RefreshDBD();
}
