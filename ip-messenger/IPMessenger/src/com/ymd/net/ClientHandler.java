/**
 * 
 */
package com.ymd.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.text.Document;

import com.ymd.log.IPMLogger;
import com.ymd.net.chat.ClientChatHandler;
import com.ymd.net.ft.ClientFileTransferHandler;
import com.ymd.net.ft.FileInfo;
import com.ymd.util.Constants;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class ClientHandler {
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private FileInfo fileInfo;
	private String ip;

	private Document doc;	
	private SSLSocketFactory sslsf=(SSLSocketFactory)SSLSocketFactory.getDefault();
	
	public ClientHandler(String ip){
		this.ip=ip;
	}
	
	public ClientHandler(String ip,FileInfo fileInfo,Document doc){
		this.ip=ip;
		this.fileInfo=fileInfo;
		this.doc=doc;
	}	
	
	public void connect(String context){		
		try{			
			SSLSocket socket= (SSLSocket)sslsf.createSocket(ip,Constants.CHAT_SERVER_PORT);
			final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
			socket.setEnabledCipherSuites(enabledCipherSuites);			
			InputStream in=socket.getInputStream();				
			ObjectInputStream ois=new ObjectInputStream(in);			
			OutputStream out=socket.getOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(out);							
			SocketInfo sockInfo=new SocketInfo(socket,oos,ois,ip);
						
			if(context.equals(Constants.CHAT)){
				Thread chatHandler=new Thread(new ClientChatHandler(sockInfo));
				chatHandler.start();
			}else if(context.equals(Constants.FILE_TRANSFER)){
				Thread ftHandler=new Thread(new ClientFileTransferHandler(sockInfo,fileInfo,doc));
				ftHandler.start();
			}
						
		}catch(UnknownHostException uhe){
			logger.error(uhe.getMessage(), uhe);			
		}catch(IOException ioe){
			logger.error(ioe.getMessage(),ioe);			
		}
	}

}
