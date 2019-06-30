package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.ImagePath;
import java.util.List;

public interface ImagePathService {

    //only read

    //have write
    public void insertImagePath(ImagePath imagePath);

    public List<ImagePath> findImagePath(Integer goodsid);
}
