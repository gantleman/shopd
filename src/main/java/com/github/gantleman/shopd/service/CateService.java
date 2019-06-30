package com.github.gantleman.shopd.service;

import com.github.gantleman.shopd.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("CateService")
public interface CateService {

    //only read
    public List<Category> selectByAll();

    public List<Category> selectByNameForRead(String cate);
    
    public Category selectById(Integer category);

    //have write
    public List<Category> selectByName(String cate);

    public void insertSelective(Category category);

    public void updateByPrimaryKeySelective(Category category);

    public void deleteByPrimaryKey(Integer cateid);

    public void TickBack();

    public void RefreshDBD();
}
