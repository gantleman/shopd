package com.github.gantleman.shopd.da;
import  com.github.gantleman.shopd.entity.ChatUser;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ChatUserDA {

	// Primary key field type, entity class
	PrimaryIndex<Integer, ChatUser> pIdx;// Primary Index

	public ChatUserDA(EntityStore entityStore) {
		// Primary key field type, entity class
		pIdx = entityStore.getPrimaryIndex(Integer.class, ChatUser.class);
	}

	/**
* Add a ChatUser
	 */
	public void saveChatUser(ChatUser chatuser) {
		pIdx.put(chatuser);
	}

	/**
	 * Delete one based on user ID ChatUser
	 **/
	public void removedChatUserById(Integer chatuserId) {
		pIdx.delete(chatuserId);
	}

	/**
	 * Find one based on user IDChatUser
	 **/
	public ChatUser findChatUserById(Integer chatuserId) {
		return pIdx.get(chatuserId);
	}

	/**
	 * Find all ChatUser
	 **/
	public List<ChatUser> findAllChatUser() {
		List<ChatUser> chatuserList = new ArrayList<ChatUser>();
		// open cursor
		EntityCursor<ChatUser> chatuserCursorList = null;
		try {
			//Get the cursor
			chatuserCursorList = pIdx.entities();
			// Traversal cursor
			for (ChatUser chatuser : chatuserCursorList) {
				chatuserList.add(chatuser);
			}
		} catch (DatabaseException e) {
			
		} finally {
			if (chatuserCursorList != null) {
				// Close the cursor
				chatuserCursorList.close();
			}
		}
		return chatuserList;
	}
	
	
	/**
	 * Statistics of all users
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
