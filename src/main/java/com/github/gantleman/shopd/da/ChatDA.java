package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class ChatDA {

	// 主键字段类型,实体类
	PrimaryIndex<Integer, Chat> pIdx;// 一级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Chat> sIdx;// 二级索引

	// 辅助键字段类型,主键字段类型,实体类
	SecondaryIndex<Integer, Integer, Chat> sIdx2;// 二级索引

	public ChatDA(EntityStore entityStore) {
		// 主键字段类型,实体类
		pIdx = entityStore.getPrimaryIndex(Integer.class, Chat.class);
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "senduser");
		// 主键索引,辅助键字段类型,辅助键字段名称
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "receiveuser");
	}

	/**
	 * 添加一个Chat
	 */
	public void saveChat(Chat chat) {
		pIdx.put(chat);
	}

	/**
	 * 根据用户Id删除一个recode
	 **/
	public void removedChatById(Integer chatId) {
		pIdx.delete(chatId);
	}

	/**
	 * 根据用户名称删除Chat
	 **/
	public void removedChatBySendUserID(Integer userid) {
		sIdx.delete(userid);
	}
	
	/**
	 * 根据用户名称删除Chat
	 **/
	public void removedChatByReceiveUserID(Integer userid) {
		sIdx2.delete(userid);
	}
	
	/**
	 * 根据用户Id查找一个Chat
	 **/
	public Chat findChatById(Integer chatId) {
		return pIdx.get(chatId);
	}

	/**
	 * 查找所有的Chat
	 **/
	public List<Chat> findAllChat() {
		List<Chat> chatList = new ArrayList<Chat>();
		// 打开游标
		EntityCursor<Chat> chatCursorList = null;
		try {
			//获取游标
			chatCursorList = pIdx.entities();
			// 遍历游标
			for (Chat chat : chatCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (chatCursorList != null) {
				// 关闭游标
				chatCursorList.close();
			}
		}
		return chatList;
	}
	
	/**
	 * 根据chatName查找所有的Chat
	 **/
	public List<Chat> findAllChatBySendUserID(Integer userid) {
	    
		List<Chat> chatList=new ArrayList<Chat>();
		
		EntityCursor<Chat> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//遍历游标
			for (Chat chat : entityCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return chatList;
	}
	

		/**
	 * 根据chatName查找所有的Chat
	 **/
	public List<Chat> findAllChatByReceiveUserID(Integer userid) {
	    
		List<Chat> chatList=new ArrayList<Chat>();
		
		EntityCursor<Chat> entityCursorList=null;
		
		//获取游标
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//遍历游标
			for (Chat chat : entityCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//关闭游标
				entityCursorList.close();
			}
		}
		return chatList;
	}

	/**
	 * 统计所有用户数
	**/
	public Long findAllChatCount() {
		Long count = 0L;
		EntityCursor<Chat> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Chat chat : cursor) {
            	if(chat!=null) {
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
	
	/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllChatBySendUserIDCount(Integer userid) {
		Long count = 0L;
		EntityCursor<Chat> cursor = null;
        try{
            cursor = sIdx.subIndex(userid).entities();
            for (Chat chat : cursor) {
            	if(chat!=null) {
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

		/**
	 * 统计所有满足用户名的用户总数
	 *****/
	public Long findAllChatByReceiveUserCount(Integer userid) {
		Long count = 0L;
		EntityCursor<Chat> cursor = null;
        try{
            cursor = sIdx2.subIndex(userid).entities();
            for (Chat chat : cursor) {
            	if(chat!=null) {
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

	public List<Chat> findAllWhitStamp(long stamp) {
		List<Chat> adminList = new ArrayList<Chat>();
		// 打开游标
		EntityCursor<Chat> adminCursorList = null;
		try {
			//获取游标
			adminCursorList = pIdx.entities();
			// 遍历游标
			for (Chat chat : adminCursorList) {
				if(chat.getStamp() <= stamp) {
					adminList.add(chat);
				}
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
		} finally {
			if (adminCursorList != null) {
				// 关闭游标
				adminCursorList.close();
			}
		}
		return adminList;
	}

	public boolean IsEmpty() {
		boolean count = true;
		EntityCursor<Chat> cursor = null;
        try{
            cursor = pIdx.entities();
            for (Chat activity : cursor) {
            	if(activity!=null) {
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
