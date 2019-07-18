package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.ImagePath;
import com.github.gantleman.shopd.entity.ImagePathExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ImagePathMapper {
    long countByExample(ImagePathExample example);

    int deleteByExample(ImagePathExample example);

    int deleteByPrimaryKey(Integer pathid);

    int insert(ImagePath record);

    int insertSelective(ImagePath record);

    List<ImagePath> selectByExample(ImagePathExample example);

    ImagePath selectByPrimaryKey(Integer pathid);

    int updateByExampleSelective(@Param("record") ImagePath record, @Param("example") ImagePathExample example);

    int updateByExample(@Param("record") ImagePath record, @Param("example") ImagePathExample example);

    int updateByPrimaryKeySelective(ImagePath record);

    int updateByPrimaryKey(ImagePath record);
}