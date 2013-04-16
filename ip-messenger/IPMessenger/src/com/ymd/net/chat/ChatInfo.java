/**
 * 
 */
package com.ymd.net.chat;

import java.io.Serializable;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class ChatInfo implements Serializable {

	private static final long serialVersionUID = -1708870186697580485L;
	
	protected String chatId;
	protected String userName;
	
	/**
	 * Constructor that sets chatId and userName
	 * @param chatId
	 * @param userName
	 */
	public ChatInfo(String chatId, String userName){
		this.chatId=chatId;
		this.userName=userName;
	}
	
	/**
	 * Constructor which takes userName.
	 * @param userName
	 */
	public ChatInfo(String userName){
		this.userName=userName;
	}

	/**
	 * @return the chatId
	 */
	public String getChatId() {
		return chatId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	

	
}
