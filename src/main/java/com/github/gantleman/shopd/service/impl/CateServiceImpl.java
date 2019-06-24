package com.github.gantleman.shopd.service.impl;

import com.github.gantleman.shopd.dao.CategoryMapper;
import com.github.gantleman.shopd.entity.Category;
import com.github.gantleman.shopd.entity.CategoryExample;
import com.github.gantleman.shopd.service.CateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 文辉 on 2017/7/23.
 */
@Service("cateService")
public class CateServiceImpl implements CateService {

    @Autowired(required = false)
    CategoryMapper categoryMapper;

    @Override
    public List<Category> selectByAll() {
        return categoryMapper.selectByExample(new CategoryExample());
    }

    @Override
    public List<Category> selectByName(String cate) {

        CategoryExample digCategoryExample = new CategoryExample();
        digCategoryExample.or().andCatenameLike(cate);

        return categoryMapper.selectByExample(digCategoryExample);
    }

    @Override
    public List<Category> selectByNameForRead(String cate) {

        CategoryExample digCategoryExample = new CategoryExample();
        digCategoryExample.or().andCatenameLike(cate);

        return categoryMapper.selectByExample(digCategoryExample);
    }

    @Override
    public void insertSelective(Category category) {
        categoryMapper.insertSelective(category);
    }
    
    @Override
    public Category selectById(Integer category) {
        return categoryMapper.selectByPrimaryKey(category);
    }

    @Override
    public void updateByPrimaryKeySelective(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public void deleteByPrimaryKey(Integer cateid) {
        categoryMapper.deleteByPrimaryKey(cateid);
    }
}
