/**
 * 
 */
package com.ymd.net.ft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.ymd.util.FileUtil;

/**
 * This handles the File Server sockets.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class FileSocketHandler implements Runnable{
	
	private Socket fileSocket;
	
	/**
	 * Constructs the FileSocketHadler object.
	 * 
	 * @param fileSocket - The socket it has to handle.
	 */
	public FileSocketHandler(Socket fileSocket){
		this.fileSocket=fileSocket;
	}

	@Override
	public void run() {
		InputStream fsInputStream=null;
		try{
			fsInputStream=fileSocket.getInputStream();
			File file=null;			
			FileOutputStream fos=null;			
			StringBuffer fileName=new StringBuffer();
			boolean fileNameChar=true;
			boolean fileValue=false;
			
			while(true){
				
				int value=fsInputStream.read();
				
				if(value==-1){	
					if(fos != null){
						fos.flush();
						fos.close();
					}
					break;
				}
				
				if(fileValue){						
					fos.write(value);
				}
				
				if(fileNameChar){
					if(((byte)value) != -2){						
						char ch=(char)value;
						fileName.append(ch);
					}else{
						file=FileUtil.createNonExistingFile(fileName.toString());												
						fos=new FileOutputStream(file);
						fileNameChar=false;
						fileValue=true;
					}
				}				
			}
			
		}catch(IOException ioe){
			ioe.printStackTrace();			
		}finally{
			try{
				fsInputStream.close();
				fileSocket.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
	}	
}
