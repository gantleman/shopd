package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("CateService")
public interface CateService {

    //only read
    public List<Category> selectByAll(Integer pageId, String url);

    public List<Category> selectByNameForRead(String catename, String url);
    
    public Category selectById(Integer category, String url);

    //have write
    public List<Category> selectByName(String catename);

    public void insertSelective(Category category);

    public void updateByPrimaryKeySelective(Category category);

    public void deleteByPrimaryKey(Integer cateid);

    public void TickBack();

    public void RefreshDBD(Integer pageID, boolean refresRedis);

    public void RefreshDBD(String name, boolean refresRedis);
}
