/**
 * 
 */
package com.ymd.net.ft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.ymd.gui.dnd.listner.UADNDListener.StatusPanels;

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
	private String chatId;
	private StatusPanels panels;
 
	/**
	 * Constructs the FileClient object.
	 * 
	 * @param ip - IP address of the destination.
	 * @param file - The files which has to be transfered.
	 */
	public FileClient(String ip,File file,String chatId,StatusPanels panels){
		this.ip=ip;
		this.file=file;
		this.chatId=chatId;
		this.panels=panels;
	}

	@Override
	public void run() {
		Socket fileSock=null;
		OutputStream socketOs=null;
		InputStream sockestIs=null;
		try{			
			String simpleFileName=file.getName();			
			fileSock=new Socket(ip,1984);
			socketOs=fileSock.getOutputStream();
			sockestIs=fileSock.getInputStream();
			FileInputStream fis=new FileInputStream(file);
			
			socketOs.write(chatId.getBytes());
			socketOs.write(-2);	
			
			socketOs.write(simpleFileName.getBytes());			
			socketOs.write(-2);	
			
			boolean cmdChar=true;
			boolean action=false;
			
			StringBuffer cmd=new StringBuffer();
			
			while(true){
				
				int value=sockestIs.read();
				
				if(value==-1)
					break;
				
				if(cmdChar){
					if(((byte)value)!=-2){
						char chr=(char)value;
						cmd.append(chr);
					}else{
						cmdChar=false;
						action=true;
					}
				}
				
				if(action){
					if(cmd.toString().equalsIgnoreCase("accept")){
						JPanel status=panels.getStatus();
						Component[] statusComps=status.getComponents();
						JTextField statustf=(JTextField)statusComps[0];
						statustf.setText("Transfer Progress...");
						
						JPanel progress=panels.getProgress();
						JProgressBar jpb=new JProgressBar();
						Long filesize=file.length();						
						jpb.setMaximum(filesize.intValue());						
						progress.add(jpb,BorderLayout.CENTER);
						
						
						Long fileSize=file.length();	
						String fleSizeStr=fileSize.toString();
						socketOs.write(fleSizeStr.getBytes());
						socketOs.write(-2);
						
						int count=1;
						while(true){
							int fileByte=fis.read();
							
							if(fileByte==-1){					
								fis.close();
								break;					
							}else{									
								socketOs.write(fileByte);
								jpb.setValue(count);
								count=count+1;
							}
						}
						
						statustf.setText("Transfer Completed..");
						
						break;
					}else if(cmd.toString().equalsIgnoreCase("reject")){
						JPanel status=panels.getStatus();
						Component[] statusComps=status.getComponents();
						JTextField statustf=(JTextField)statusComps[0];
						statustf.setText("Transfer Rejected...");
						break;
					}
				}				
			}			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			try{
				sockestIs.close();
				socketOs.close();
				fileSock.close();				
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}	
}
