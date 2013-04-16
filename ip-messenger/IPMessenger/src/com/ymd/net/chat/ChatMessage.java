package com.ymd.net.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable{
	
	private static final long serialVersionUID = 2438690220324476254L;	
	
	private String message;	
	
	public ChatMessage(String message){		
		this.message=message;		
	}
	
	
	public String getMessage() {
		return message;
	}
}
