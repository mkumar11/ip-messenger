package com.ymd.net.chat;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

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
	
	private SSLServerSocketFactory sslssf=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

	@Override
	public void run() {
		try{
			SSLServerSocket ss=(SSLServerSocket)sslssf.createServerSocket(Constants.CHAT_SERVER_PORT);
			final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
			ss.setEnabledCipherSuites(enabledCipherSuites);			
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
