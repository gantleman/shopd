package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.OrderUser;
import com.github.gantleman.shopd.entity.OrderUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderUserMapper {
    long countByExample(OrderUserExample example);

    int deleteByExample(OrderUserExample example);

    int deleteByPrimaryKey(Integer userid);

    int insert(OrderUser record);

    int insertSelective(OrderUser record);

    List<OrderUser> selectByExampleWithBLOBs(OrderUserExample example);

    List<OrderUser> selectByExample(OrderUserExample example);

    OrderUser selectByPrimaryKey(Integer userid);

    int updateByExampleSelective(@Param("record") OrderUser record, @Param("example") OrderUserExample example);

    int updateByExampleWithBLOBs(@Param("record") OrderUser record, @Param("example") OrderUserExample example);

    int updateByExample(@Param("record") OrderUser record, @Param("example") OrderUserExample example);

    int updateByPrimaryKeySelective(OrderUser record);

    int updateByPrimaryKeyWithBLOBs(OrderUser record);

    int updateByPrimaryKey(OrderUser record);
}