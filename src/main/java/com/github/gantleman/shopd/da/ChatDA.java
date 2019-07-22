package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.Chat;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class ChatDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, Chat> pIdx;// Primary Index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Chat> sIdx;// Secondary index

	// Auxiliary key field type,Primary key field type, entity class
	SecondaryIndex<Integer, Integer, Chat> sIdx2;// Secondary index

	public ChatDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, Chat.class);
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx = entityStore.getSecondaryIndex(pIdx, Integer.class, "senduser");
		// primary key,Auxiliary key field type,Auxiliary key field name
		sIdx2 = entityStore.getSecondaryIndex(pIdx, Integer.class, "receiveuser");
	}

	/**
* Add a Chat
	 */
	public void saveChat(Chat chat) {
		pIdx.put(chat);
	}

	/**
	 * Delete one based on user ID recode
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
	 * Find one based on user IDChat
	 **/
	public Chat findChatById(Integer chatId) {
		return pIdx.get(chatId);
	}

	/**
	 * Find all Chat
	 **/
	public List<Chat> findAllChat() {
		List<Chat> chatList = new ArrayList<Chat>();
		// open cursor
		EntityCursor<Chat> chatCursorList = null;
		try {
			//Get the cursor
			chatCursorList = pIdx.entities();
			// Traversal cursor
			for (Chat chat : chatCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
		} finally {
			if (chatCursorList != null) {
				// Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx.subIndex(userid).entities();
			//Traversal cursor
			for (Chat chat : entityCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
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
		
		//Get the cursor
		try {
			entityCursorList=sIdx2.subIndex(userid).entities();
			//Traversal cursor
			for (Chat chat : entityCursorList) {
				chatList.add(chat);
			}
		} catch (DatabaseException e) {
			
			e.printStackTrace();
		}finally {
			if(entityCursorList!=null) {
				//Close the cursor
				entityCursorList.close();
			}
		}
		return chatList;
	}

	/**
	 * Statistics of all users
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
	 *Statistics the total number of users who satisfy the username
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
	 *Statistics the total number of users who satisfy the username
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
