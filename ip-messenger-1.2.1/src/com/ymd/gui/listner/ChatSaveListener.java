/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextPane;

import com.ymd.log.IPMLogger;
import com.ymd.util.Constants;

/**
 * This is the Listener class for ChatGui's save chat MenuItem.
 * This saves the entire chat as text file.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class ChatSaveListener implements ActionListener{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private JTextPane mainArea;
	
	/**
	 * Constructs ChatSaveListener instance.
	 * @param mainArea - where the chat is displayed.
	 */
	public ChatSaveListener(JTextPane mainArea){
		this.mainArea=mainArea;
	}

	
	public void actionPerformed(ActionEvent ae){
		Date date=new Date();
		DateFormat df= new SimpleDateFormat("dd.MM.yyyy.kk.mm.ss");
		String strDate=df.format(date);			
		String fileName="IPMChat("+strDate+").txt";
		String chatsaveLocation=System.getProperty(Constants.CHAT_FILE_DIR_KEY);
		fileName=chatsaveLocation+"\\"+fileName;
		File chatFile=new File(fileName);		
		FileWriter fw=null;		
		try{
			fw=new FileWriter(chatFile);
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}
		StringReader sr=new StringReader(mainArea.getText());
		while(true){
			try{
				int value=sr.read();
				
				if(value==-1)
					break;
				
				fw.write(value);
				
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}
		try{
			fw.flush();
			fw.close();
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}
	}
}
