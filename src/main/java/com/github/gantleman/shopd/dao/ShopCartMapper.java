package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.ShopCart;
import com.github.gantleman.shopd.entity.ShopCartExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ShopCartMapper {
    long countByExample(ShopCartExample example);

    int deleteByExample(ShopCartExample example);

    int deleteByPrimaryKey(Integer shopcartid);

    int insert(ShopCart record);

    int insertSelective(ShopCart record);

    List<ShopCart> selectByExample(ShopCartExample example);

    ShopCart selectByPrimaryKey(Integer shopcartid);

    int updateByExampleSelective(@Param("record") ShopCart record, @Param("example") ShopCartExample example);

    int updateByExample(@Param("record") ShopCart record, @Param("example") ShopCartExample example);

    int updateByPrimaryKeySelective(ShopCart record);

    int updateByPrimaryKey(ShopCart record);
}