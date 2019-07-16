package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.CommentGoods;
import com.github.gantleman.shopd.entity.CommentGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommentGoodsMapper {
    long countByExample(CommentGoodsExample example);

    int deleteByExample(CommentGoodsExample example);

    int deleteByPrimaryKey(Integer goodsid);

    int insert(CommentGoods record);

    int insertSelective(CommentGoods record);

    List<CommentGoods> selectByExampleWithBLOBs(CommentGoodsExample example);

    List<CommentGoods> selectByExample(CommentGoodsExample example);

    CommentGoods selectByPrimaryKey(Integer goodsid);

    int updateByExampleSelective(@Param("record") CommentGoods record, @Param("example") CommentGoodsExample example);

    int updateByExampleWithBLOBs(@Param("record") CommentGoods record, @Param("example") CommentGoodsExample example);

    int updateByExample(@Param("record") CommentGoods record, @Param("example") CommentGoodsExample example);

    int updateByPrimaryKeySelective(CommentGoods record);

    int updateByPrimaryKeyWithBLOBs(CommentGoods record);

    int updateByPrimaryKey(CommentGoods record);
}