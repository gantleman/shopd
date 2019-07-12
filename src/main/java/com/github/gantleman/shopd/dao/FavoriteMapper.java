package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.Favorite;
import com.github.gantleman.shopd.entity.FavoriteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FavoriteMapper {
    long countByExample(FavoriteExample example);

    int deleteByExample(FavoriteExample example);

    int deleteByPrimaryKey(Integer favoriteid);

    int insert(Favorite record);

    int insertSelective(Favorite record);

    List<Favorite> selectByExample(FavoriteExample example);

    Favorite selectByPrimaryKey(Integer favoriteid);

    int updateByExampleSelective(@Param("record") Favorite record, @Param("example") FavoriteExample example);

    int updateByExample(@Param("record") Favorite record, @Param("example") FavoriteExample example);

    int updateByPrimaryKeySelective(Favorite record);

    int updateByPrimaryKey(Favorite record);
}