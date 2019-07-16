package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.ShopcartUser;
import com.github.gantleman.shopd.entity.ShopcartUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ShopcartUserMapper {
    long countByExample(ShopcartUserExample example);

    int deleteByExample(ShopcartUserExample example);

    int deleteByPrimaryKey(Integer userid);

    int insert(ShopcartUser record);

    int insertSelective(ShopcartUser record);

    List<ShopcartUser> selectByExampleWithBLOBs(ShopcartUserExample example);

    List<ShopcartUser> selectByExample(ShopcartUserExample example);

    ShopcartUser selectByPrimaryKey(Integer userid);

    int updateByExampleSelective(@Param("record") ShopcartUser record, @Param("example") ShopcartUserExample example);

    int updateByExampleWithBLOBs(@Param("record") ShopcartUser record, @Param("example") ShopcartUserExample example);

    int updateByExample(@Param("record") ShopcartUser record, @Param("example") ShopcartUserExample example);

    int updateByPrimaryKeySelective(ShopcartUser record);

    int updateByPrimaryKeyWithBLOBs(ShopcartUser record);

    int updateByPrimaryKey(ShopcartUser record);
}