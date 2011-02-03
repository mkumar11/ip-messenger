/**
 * 
 */
package com.ymd.net.ft;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the server which handles File transfers.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class FileServer implements Runnable{


	@Override
	public void run() {
		try{
			ServerSocket fileServer=new ServerSocket(1984);
			while(true){
				Socket fileSocket=fileServer.accept();
				Thread thread=new Thread(new FileSocketHandler(fileSocket));
				thread.start();
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}		
	}	
}
