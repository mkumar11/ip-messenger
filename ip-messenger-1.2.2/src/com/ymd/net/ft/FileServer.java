/**
 * 
 */
package com.ymd.net.ft;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.ymd.log.IPMLogger;
import com.ymd.util.Constants;

/**
 * This is the server which handles File transfers.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class FileServer implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private SSLServerSocketFactory sslssf=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

	@Override
	public void run() {
		try{
			SSLServerSocket fileServer=(SSLServerSocket)sslssf.createServerSocket(Constants.FILE_SERVER_PORT);
			final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
			fileServer.setEnabledCipherSuites(enabledCipherSuites);	
			
			while(true){
				Socket fileSocket=fileServer.accept();
				Thread thread=new Thread(new FileSocketHandler(fileSocket));
				thread.start();
			}
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}		
	}	
}
