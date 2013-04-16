package com.ymd.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.ymd.log.IPMLogger;
import com.ymd.net.chat.ChatInfo;
import com.ymd.net.chat.ServerChatHandler;
import com.ymd.net.ft.FileInfo;
import com.ymd.net.ft.ServerFileTransferHandler;
import com.ymd.util.Constants;


/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class IPMsgServer implements Runnable{	
	
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
				OutputStream sos=s.getOutputStream();				
				ObjectOutputStream oos=new ObjectOutputStream(sos);
				oos.flush();
				InputStream sis= s.getInputStream();	
				ObjectInputStream ois=new ObjectInputStream(sis);					
				try{
					SocketInfo sockInfo=new SocketInfo(s,oos,ois,s.getInetAddress().getHostAddress());
					Object obj=ois.readObject();
					if(obj instanceof ChatInfo){
						ChatInfo info=(ChatInfo)obj;
						Thread chatHandler=new Thread(new ServerChatHandler(sockInfo,info));
						chatHandler.start();
					}else if(obj instanceof FileInfo){
						FileInfo fileInfo=(FileInfo)obj;
						Thread fileHandler=new Thread(new ServerFileTransferHandler(sockInfo,fileInfo));
						fileHandler.start();
					}
				}catch(ClassNotFoundException cnfe){
					logger.error(cnfe.getMessage(),cnfe);
				}				
			}
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}
	}
	
	

}
