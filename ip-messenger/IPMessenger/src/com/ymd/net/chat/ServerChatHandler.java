package com.ymd.net.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.chat.ChatGui;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.Event;
import com.ymd.net.SocketInfo;
import com.ymd.util.Constants;

/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class ServerChatHandler implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private SocketInfo sockInfo;
	private ChatInfo chatInfo;
	
	/**
	 * Constructs CSSocketHandler instance.
	 * 
	 * @param socket - Socket.
	 */
	public ServerChatHandler(SocketInfo sockInfo,ChatInfo chatInfo){
		this.sockInfo=sockInfo;
		this.chatInfo=chatInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {			
		ChatGui chat=null;
		String chatId=null;
		String ip=null;
		try{			
			chat=new ChatGui(sockInfo);
			chat.setRemoteUserName(chatInfo.getUserName());
			chat.setTitle(chatInfo.getUserName()+sockInfo.getRemoteIP());
			chatId=chatInfo.getChatId();
			ip=sockInfo.getRemoteIP();
			chat.setId(chatId);	
			if(IPMessenger.ipChatGuiIdMap.containsKey(ip)){
				List<String> chatIdList=IPMessenger.ipChatGuiIdMap.get(ip);
				chatIdList.add(chatId);				
			}else{
				List<String> chatIdList=new ArrayList<String>();
				chatIdList.add(chatId);
				IPMessenger.ipChatGuiIdMap.put(ip, chatIdList);
			}
			String userName=System.getProperty("user.name");
			ChatInfo chatInfo=new ChatInfo(userName);
			sockInfo.getSocketOOS().writeObject(chatInfo);
			
			InetAddress remoteUserAdd=sockInfo.getSocket().getInetAddress();
			ip=remoteUserAdd.getHostAddress();
			
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();			
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);		
			
			Thread blinkThread=null;
			
			while(true){	
				try{
					Object obj=sockInfo.getSocketOIS().readObject();	
					
					if(obj instanceof ChatMessage){
						ChatMessage message=(ChatMessage)obj;
						try{
							if(!chat.isVisible()){
								chat.setVisible(true);	
								chat.toBack();
							}							
							doc.insertString(doc.getLength(), chat.getRemoteUserName()+" : ",bold);
							doc.insertString(doc.getLength(), message.getMessage()+"\n",null);
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
					}else if(obj instanceof Event){
						Event event=(Event)obj;
						if(event.getEvent().equals(Constants.EVENT_WINDOW_CLOSED))
							chat.setRemoteUserClosed(true);
					}					
				}catch(ClassNotFoundException cnfe){
					logger.error(cnfe.getMessage(),cnfe);
				}
			}			
		}catch(IOException ioe){
			logger.info("Server Chat Thread gracefully closed.");			
		}finally{
			try{
				chat.dispose();
				List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(ip);
				chatIds.remove(chatId);				
				IPMessenger.chatGuiMap.remove(chat.getId());
				chat.setRemoteUserClosed(true);
				sockInfo.getSocketOIS().close();
				sockInfo.getSocketOOS().close();
				sockInfo.getSocket().close();
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}		
	}	
}
