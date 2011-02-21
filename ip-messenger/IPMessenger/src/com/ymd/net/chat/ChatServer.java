package com.ymd.net.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.ymd.log.IPMLogger;
import com.ymd.util.Constants;


/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class ChatServer implements Runnable{	
	
	private IPMLogger logger=IPMLogger.getLogger();

	@Override
	public void run() {
		try{
			ServerSocket ss=new ServerSocket(Constants.CHAT_SERVER_PORT);
			while(true){
				Socket s=ss.accept();
				Thread t=new Thread(new CSSocketHandler(s));
				t.start();
			}
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}
	}
	
	

}
