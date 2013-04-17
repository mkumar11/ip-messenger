package com.ymd.net.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.chat.ChatGui;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.Event;
import com.ymd.net.SocketInfo;
import com.ymd.util.Constants;
import com.ymd.util.GUID;

/**
 * This is the thread which runs the chat client 
 * window.
 * 
 * @author yaragalla Muralidhar
 * 
 */
public class ClientChatHandler implements Runnable{	
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private SocketInfo sockInfo;
	
	
	/**
	 * Constructs ClientAndroidChatHandler object.
	 * 
	 * @param info - SocketInfo
	 * 
	 */
	public ClientChatHandler(SocketInfo info){		
		sockInfo=info;
	}	

	@Override
	public void run() {		
		ChatGui chat=null;
		String chatId=null;
		try{
			JFrame statusFrame=GUIUtil.displayMessage(IPMessenger.mainGui.getX(), IPMessenger.mainGui.getY(),
					IPMessenger.resources.getString("establishingConnWait"));			
			
			chatId=GUID.generateId();			
			String userName=System.getProperty("user.name");
			ChatInfo info=new ChatInfo(chatId,userName);
			sockInfo.getSocketOOS().writeObject(info);
			statusFrame.dispose();
			
			chat=new ChatGui(sockInfo);			
			chat.setId(chatId);
			if(IPMessenger.ipChatGuiIdMap.containsKey(sockInfo.getRemoteIP())){
				List<String> chatIdList=IPMessenger.ipChatGuiIdMap.get(sockInfo.getRemoteIP());
				chatIdList.add(chatId);				
			}else{
				List<String> chatIdList=new ArrayList<String>();
				chatIdList.add(chatId);
				IPMessenger.ipChatGuiIdMap.put(sockInfo.getRemoteIP(), chatIdList);
			}
			chat.setVisible(true);
			
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);		
			
			Thread blinkThread=null;
			
			while(true){
				try{
					Object obj=sockInfo.getSocketOIS().readObject();			
					
					if(obj instanceof Event){
						Event event=(Event)obj;
						if(event.getEvent().equals(Constants.EVENT_WINDOW_CLOSED))
							chat.setRemoteUserClosed(true);
					}else if(obj instanceof ChatMessage){
						ChatMessage cm=(ChatMessage)obj;
						try{
							if(!chat.isVisible()){
								chat.setVisible(true);
								chat.toBack();
							}
							doc.insertString(doc.getLength(), chat.getRemoteUserName()+" : ",bold);
							doc.insertString(doc.getLength(), cm.getMessage()+"\n",null);
							mainArea.setCaretPosition(doc.getLength());	
							if(!chat.isFocused()){
								if(blinkThread==null ||(!blinkThread.isAlive())){
									ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
									blinkThread=new Thread(new GuiIconBlink(chat,IPMessenger.blinkImages,icon.getImage()));
									blinkThread.start();
								}
							}
						}catch(BadLocationException ble){
							logger.error(ble.getMessage(), ble);
						}
					}else if(obj instanceof ChatInfo){
						ChatInfo remoteInfo=(ChatInfo)obj;
						chat.setRemoteUserName(remoteInfo.getUserName());
						chat.setTitle(remoteInfo.getUserName()+":"+sockInfo.getRemoteIP());
					}		
				}catch(ClassNotFoundException cnfe){
					logger.error(cnfe.getMessage(), cnfe);
				}
			}// End of While Loop
		}catch(IOException ioe){
			logger.error(ioe.getMessage(),ioe);			
			logger.info("Client Chat Thread gracefully closed.");			
		}finally{
			try{	
				chat.dispose();
				List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(sockInfo.getRemoteIP());
				chatIds.remove(chatId);	
				chat.setRemoteUserClosed(true);
				IPMessenger.chatGuiMap.remove(chat.getId());
				sockInfo.getSocketOOS().close();
				sockInfo.getSocketOIS().close();
				sockInfo.getSocket().close();	
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}		
	}
}
