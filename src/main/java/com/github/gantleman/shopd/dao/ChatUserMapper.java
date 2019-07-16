package com.github.gantleman.shopd.dao;

import com.github.gantleman.shopd.entity.ChatUser;
import com.github.gantleman.shopd.entity.ChatUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ChatUserMapper {
    long countByExample(ChatUserExample example);

    int deleteByExample(ChatUserExample example);

    int deleteByPrimaryKey(Integer userid);

    int insert(ChatUser record);

    int insertSelective(ChatUser record);

    List<ChatUser> selectByExampleWithBLOBs(ChatUserExample example);

    List<ChatUser> selectByExample(ChatUserExample example);

    ChatUser selectByPrimaryKey(Integer userid);

    int updateByExampleSelective(@Param("record") ChatUser record, @Param("example") ChatUserExample example);

    int updateByExampleWithBLOBs(@Param("record") ChatUser record, @Param("example") ChatUserExample example);

    int updateByExample(@Param("record") ChatUser record, @Param("example") ChatUserExample example);

    int updateByPrimaryKeySelective(ChatUser record);

    int updateByPrimaryKeyWithBLOBs(ChatUser record);

    int updateByPrimaryKey(ChatUser record);
}