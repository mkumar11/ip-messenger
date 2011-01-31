package com.ymd.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class ChatServer implements Runnable{	

	@Override
	public void run() {
		try{
			ServerSocket ss=new ServerSocket(1986);
			while(true){
				Socket s=ss.accept();
				Thread t=new Thread(new TSocketHandler(s));
				t.start();
			}
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	

}
