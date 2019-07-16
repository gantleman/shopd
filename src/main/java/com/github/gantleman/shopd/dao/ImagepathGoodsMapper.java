package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.ImagepathGoods;
import com.github.gantleman.shopd.entity.ImagepathGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ImagepathGoodsMapper {
    long countByExample(ImagepathGoodsExample example);

    int deleteByExample(ImagepathGoodsExample example);

    int deleteByPrimaryKey(Integer goodsid);

    int insert(ImagepathGoods record);

    int insertSelective(ImagepathGoods record);

    List<ImagepathGoods> selectByExampleWithBLOBs(ImagepathGoodsExample example);

    List<ImagepathGoods> selectByExample(ImagepathGoodsExample example);

    ImagepathGoods selectByPrimaryKey(Integer goodsid);

    int updateByExampleSelective(@Param("record") ImagepathGoods record, @Param("example") ImagepathGoodsExample example);

    int updateByExampleWithBLOBs(@Param("record") ImagepathGoods record, @Param("example") ImagepathGoodsExample example);

    int updateByExample(@Param("record") ImagepathGoods record, @Param("example") ImagepathGoodsExample example);

    int updateByPrimaryKeySelective(ImagepathGoods record);

    int updateByPrimaryKeyWithBLOBs(ImagepathGoods record);

    int updateByPrimaryKey(ImagepathGoods record);
}