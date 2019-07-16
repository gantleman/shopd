package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.FavoriteUser;
import com.github.gantleman.shopd.entity.FavoriteUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FavoriteUserMapper {
    long countByExample(FavoriteUserExample example);

    int deleteByExample(FavoriteUserExample example);

    int deleteByPrimaryKey(Integer userid);

    int insert(FavoriteUser record);

    int insertSelective(FavoriteUser record);

    List<FavoriteUser> selectByExampleWithBLOBs(FavoriteUserExample example);

    List<FavoriteUser> selectByExample(FavoriteUserExample example);

    FavoriteUser selectByPrimaryKey(Integer userid);

    int updateByExampleSelective(@Param("record") FavoriteUser record, @Param("example") FavoriteUserExample example);

    int updateByExampleWithBLOBs(@Param("record") FavoriteUser record, @Param("example") FavoriteUserExample example);

    int updateByExample(@Param("record") FavoriteUser record, @Param("example") FavoriteUserExample example);

    int updateByPrimaryKeySelective(FavoriteUser record);

    int updateByPrimaryKeyWithBLOBs(FavoriteUser record);

    int updateByPrimaryKey(FavoriteUser record);
}