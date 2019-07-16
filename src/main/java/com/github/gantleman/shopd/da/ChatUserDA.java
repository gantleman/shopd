package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ChatUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ChatUserDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, ChatUser> pIdx;// 一级索引

	public ChatUserDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, ChatUser.class);
	}

	/**
	 * 添加一个ChatUser
	 */
	public void saveChatUser(ChatUser chatuser) {
		pIdx.put(chatuser);
	}

	/**
	 * 根据用户Id删除一个ChatUser
	 **/
	public void removedChatUserById(Integer chatuserId) {
		pIdx.delete(chatuserId);
	}

	/**
	 * 根据用户Id查找一个ChatUser
	 **/
	public ChatUser findChatUserById(Integer chatuserId) {
		return pIdx.get(chatuserId);
	}

	/**
	 * 查找所有的ChatUser
	 **/
	public List<ChatUser> findAllChatUser() {
		List<ChatUser> chatuserList = new ArrayList<ChatUser>();
		// 打开游标
		EntityCursor<ChatUser> chatuserCursorList = null;
		try {
			//获取游标
			chatuserCursorList = pIdx.entities();
			// 遍历游标
			for (ChatUser chatuser : chatuserCursorList) {
				chatuserList.add(chatuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (chatuserCursorList != null) {
				// 关闭游标
				chatuserCursorList.close();
			}
		}
		return chatuserList;
	}
	
	
	/**
	 * 统计所有用户数
	**/
	public Long findAllChatUserCount() {
		Long count = 0L;
		EntityCursor<ChatUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ChatUser chatuser : cursor) {
            	if(chatuser!=null) {
            		count++;
            	}
			}
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
		return count;
	}

	public boolean IsEmpty() {
		boolean count = true;
		EntityCursor<ChatUser> cursor = null;
        try{
            cursor = pIdx.entities();
            for (ChatUser chatuser : cursor) {
            	if(chatuser!=null) {
					count = false;
					break;
            	}
			}
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
		return count;
	}
}
