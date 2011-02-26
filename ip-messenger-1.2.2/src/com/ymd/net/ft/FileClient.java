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

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.ymd.gui.dnd.listner.UADNDListener.StatusPanels;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.util.Constants;

/**
 * This establishes connection to the given IP and transfers the file
 * to the destined IP.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class FileClient implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private File file;
	private String ip;
	private String chatId;
	private StatusPanels panels;
	private SSLSocketFactory sslsf=(SSLSocketFactory)SSLSocketFactory.getDefault();
 
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
		SSLSocket fileSock=null;
		OutputStream socketOs=null;
		InputStream sockestIs=null;
		try{			
			String simpleFileName=file.getName();
			
			fileSock= (SSLSocket)sslsf.createSocket(ip,Constants.FILE_CLIENT_PORT);
			final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
			fileSock.setEnabledCipherSuites(enabledCipherSuites);			
			
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
						statustf.setText(IPMessenger.resources.getString("progressFT"));
						
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
						statustf.setText(IPMessenger.resources.getString("completedFT"));						
						break;
						
					}else if(cmd.toString().equalsIgnoreCase("reject")){
						JPanel status=panels.getStatus();
						Component[] statusComps=status.getComponents();
						JTextField statustf=(JTextField)statusComps[0];
						statustf.setText(IPMessenger.resources.getString("rejectedFT"));
						break;
					}
				}				
			}			
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}finally{
			try{
				sockestIs.close();
				socketOs.close();
				fileSock.close();				
			}catch(IOException ioe){
				logger.error(ioe.getMessage(),ioe);
			}
		}		
	}	
}
