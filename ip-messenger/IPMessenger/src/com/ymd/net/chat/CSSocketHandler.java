package com.ymd.net.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.ChatGui;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;

/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class CSSocketHandler implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private Socket socket;
	
	/**
	 * Constructs CSSocketHandler instance.
	 * 
	 * @param socket - Socket.
	 */
	public CSSocketHandler(Socket socket){
		this.socket=socket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		InputStream is=null;
		OutputStream out=null;
		ChatGui chat=null;
		String chatId=null;
		String ip=null;
		try{			
			is=socket.getInputStream();			
			out=socket.getOutputStream();						
			
			chat=new ChatGui(socket);
			
			InetAddress remoteUserAdd=socket.getInetAddress();
			ip=remoteUserAdd.getHostAddress();
			
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();			
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			
			StringBuffer msg=new StringBuffer();
			StringBuffer id=new StringBuffer();
			StringBuffer userName=new StringBuffer();
			
			boolean idValue=true;
			boolean msgValue=false;
			boolean userNameValue=false;
			
			Thread blinkThread=null;
			
			while(true){									
				int value=is.read();	
				
				if(value==-1){
					chat.dispose();
					break;
				}
				
				if(((byte)value) ==-1){
					chat.setRemoteUserClosed(true);
				}
				
				if(msgValue){
					if(((byte)value) !=-2 && ((byte)value) !=-1){
						char ch=(char)value;
						msg.append(ch);
					}else if(((byte)value) !=-1){
						try{
							if(!chat.isVisible()){
								chat.setVisible(true);	
								chat.toBack();
							}							
							doc.insertString(doc.getLength(), chat.getRemoteUserName()+" : ",bold);
							doc.insertString(doc.getLength(), msg.toString()+"\n",null);
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
						msg=new StringBuffer();
					}
				}
				
				//collecting the User name bytes.
				if(userNameValue){
					if(((byte)value)==-2){
						chat.setRemoteUserName(userName.toString());
						chat.setTitle(userName.toString());
						String user=System.getProperty("user.name");
						out.write(user.getBytes());
						out.write(-2);						
						userNameValue=false;
						msgValue=true;
					}else{
						char chr=(char)value;
						userName.append(chr);
					}
				}
				
				// collects the ID bytes.
				if(idValue){
					if(((byte)value)==-2){
						chatId=id.toString();
						chat.setId(chatId);	
						if(IPMessenger.ipChatGuiIdMap.containsKey(ip)){
							List<String> chatIdList=IPMessenger.ipChatGuiIdMap.get(ip);
							chatIdList.add(chatId);				
						}else{
							List<String> chatIdList=new ArrayList<String>();
							chatIdList.add(chatId);
							IPMessenger.ipChatGuiIdMap.put(ip, chatIdList);
						}
						idValue=false;
						userNameValue=true;
					}else{
						char chr=(char)value;
						id.append(chr);
					}
				}
			}			
		}catch(IOException ioe){
			logger.info("Thread gracefully closed.");			
		}finally{
			try{
				List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(ip);
				chatIds.remove(chatId);				
				IPMessenger.chatGuiMap.remove(chat.getId());
				chat.setRemoteUserClosed(true);
				is.close();
				out.close();
				socket.close();
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}		
	}	
}
