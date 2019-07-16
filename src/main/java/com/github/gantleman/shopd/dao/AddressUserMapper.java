package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.AddressUser;
import com.github.gantleman.shopd.entity.AddressUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AddressUserMapper {
    long countByExample(AddressUserExample example);

    int deleteByExample(AddressUserExample example);

    int deleteByPrimaryKey(Integer userid);

    int insert(AddressUser record);

    int insertSelective(AddressUser record);

    List<AddressUser> selectByExampleWithBLOBs(AddressUserExample example);

    List<AddressUser> selectByExample(AddressUserExample example);

    AddressUser selectByPrimaryKey(Integer userid);

    int updateByExampleSelective(@Param("record") AddressUser record, @Param("example") AddressUserExample example);

    int updateByExampleWithBLOBs(@Param("record") AddressUser record, @Param("example") AddressUserExample example);

    int updateByExample(@Param("record") AddressUser record, @Param("example") AddressUserExample example);

    int updateByPrimaryKeySelective(AddressUser record);

    int updateByPrimaryKeyWithBLOBs(AddressUser record);

    int updateByPrimaryKey(AddressUser record);
}