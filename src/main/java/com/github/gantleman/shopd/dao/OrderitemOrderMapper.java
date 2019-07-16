package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.OrderitemOrder;
import com.github.gantleman.shopd.entity.OrderitemOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderitemOrderMapper {
    long countByExample(OrderitemOrderExample example);

    int deleteByExample(OrderitemOrderExample example);

    int deleteByPrimaryKey(Integer orderid);

    int insert(OrderitemOrder record);

    int insertSelective(OrderitemOrder record);

    List<OrderitemOrder> selectByExampleWithBLOBs(OrderitemOrderExample example);

    List<OrderitemOrder> selectByExample(OrderitemOrderExample example);

    OrderitemOrder selectByPrimaryKey(Integer orderid);

    int updateByExampleSelective(@Param("record") OrderitemOrder record, @Param("example") OrderitemOrderExample example);

    int updateByExampleWithBLOBs(@Param("record") OrderitemOrder record, @Param("example") OrderitemOrderExample example);

    int updateByExample(@Param("record") OrderitemOrder record, @Param("example") OrderitemOrderExample example);

    int updateByPrimaryKeySelective(OrderitemOrder record);

    int updateByPrimaryKeyWithBLOBs(OrderitemOrder record);

    int updateByPrimaryKey(OrderitemOrder record);
}