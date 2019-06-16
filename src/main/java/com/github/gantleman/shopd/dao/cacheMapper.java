package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.Cache;
import com.github.gantleman.shopd.entity.CacheExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CacheMapper {
    long countByExample(CacheExample example);

    int deleteByExample(CacheExample example);

    int deleteByPrimaryKey(Integer cId);

    int insert(Cache record);

    int insertSelective(Cache record);

    List<Cache> selectByExample(CacheExample example);

    Cache selectByPrimaryKey(Integer cId);

    int updateByExampleSelective(@Param("record") Cache record, @Param("example") CacheExample example);

    int updateByExample(@Param("record") Cache record, @Param("example") CacheExample example);

    int updateByPrimaryKeySelective(Cache record);

    int updateByPrimaryKey(Cache record);
}