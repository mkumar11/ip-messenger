/**
 * 
 */
package com.ymd.net.ft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This establishes connection to the given IP and transfers the file
 * to the destined IP.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class FileClient implements Runnable{
	
	private File file;
	private String ip;

	public FileClient(String ip,File file){
		this.ip=ip;
		this.file=file;
	}

	@Override
	public void run() {
		Socket fileSock=null;
		OutputStream socketos=null;
		try{			
			String simpleFileName=file.getName();			
			fileSock=new Socket(ip,1984);
			socketos=fileSock.getOutputStream();
			FileInputStream fis=new FileInputStream(file);
			
			socketos.write(simpleFileName.getBytes());			
			socketos.write(-2);				
			
			while(true){
				int value=fis.read();
				
				if(value==-1){					
					fis.close();
					break;					
				}else{									
					socketos.write(value);
				}
				
			}			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			try{
				socketos.close();
				fileSock.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}	
}
