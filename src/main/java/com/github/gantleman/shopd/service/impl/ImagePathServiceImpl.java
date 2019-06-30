package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.ImagePathMapper;
import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.ImagePathExample;
import com.github.gantleman.shopd.service.ImagePathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("imagePathService")
public class ImagePathServiceImpl implements ImagePathService {

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Override
    public void insertImagePath(ImagePath imagePath) {
        imagePathMapper.insertSelective(imagePath);
    }

    @Override
    public List<ImagePath> findImagePath(Integer goodsid) {
        ImagePathExample imagePathExample = new ImagePathExample();
        imagePathExample.or().andGoodidEqualTo(goodsid);

        return imagePathMapper.selectByExample(imagePathExample);
    }
}
