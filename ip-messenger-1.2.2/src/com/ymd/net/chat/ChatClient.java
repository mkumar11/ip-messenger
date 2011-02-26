package com.ymd.net.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.ChatGui;
import com.ymd.gui.MainGui;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.util.Constants;
import com.ymd.util.GUID;

/**
 * This is the thread which runs the chat client 
 * window.
 * 
 * @author yaragalla Muralidhar
 * 
 */
public class ChatClient implements Runnable{	
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private String ip;
	private MainGui mainGui;
	private SSLSocketFactory sslsf=(SSLSocketFactory)SSLSocketFactory.getDefault();
	
	/**
	 * Constructs ChatClient object.
	 * 
	 * @param ip - destination IP address.
	 * @param mainGui - the mainGui of the application.
	 */
	public ChatClient(String ip,MainGui mainGui){		
		this.ip=ip;
		this.mainGui=mainGui;
	}
	

	@Override
	public void run() {
		SSLSocket socket=null;
		InputStream in=null;
		OutputStream out=null;
		ChatGui chat=null;
		String chatId=null;
		try{
			JFrame statusFrame=GUIUtil.displayMessage(mainGui.getX(), mainGui.getY(),
					IPMessenger.resources.getString("establishingConnWait"));
			
			socket= (SSLSocket)sslsf.createSocket(ip,Constants.CHAT_CLIENT_PORT);
			final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
			socket.setEnabledCipherSuites(enabledCipherSuites);
			
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			//sending the same id to the destination IP.
			chatId=GUID.generateId();			
			out.write(chatId.getBytes());
			out.write(-2);
			
			//sending the user name.
			String user=System.getProperty("user.name");
			out.write(user.getBytes());
			out.write(-2);
			
			statusFrame.dispose();
			chat=new ChatGui(socket);			
			chat.setId(chatId);
			if(IPMessenger.ipChatGuiIdMap.containsKey(ip)){
				List<String> chatIdList=IPMessenger.ipChatGuiIdMap.get(ip);
				chatIdList.add(chatId);				
			}else{
				List<String> chatIdList=new ArrayList<String>();
				chatIdList.add(chatId);
				IPMessenger.ipChatGuiIdMap.put(ip, chatIdList);
			}
			
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			
			StringBuffer msg=new StringBuffer();
			StringBuffer userName=new StringBuffer();
			
			boolean userNameValue=true;
			boolean msgValue=false;
			
			Thread blinkThread=null;
			
			while(true){
				int value=in.read();	
				
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
				
				//collecting the user name bytes.
				if(userNameValue){
					if(((byte)value) !=-2){
						char ch=(char)value;
						userName.append(ch);
					}else{
						chat.setRemoteUserName(userName.toString());
						chat.setTitle(userName.toString());
						chat.setVisible(true); 
						userNameValue=false;
						msgValue=true;
					}
				}				
			}
		}catch(IOException ioe){
			logger.info("Thread gracefully closed.");			
		}finally{
			try{				
				List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(ip);
				chatIds.remove(chatId);	
				chat.setRemoteUserClosed(true);
				IPMessenger.chatGuiMap.remove(chat.getId());
				in.close();
				out.close();
				socket.close();	
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}		
	}
}
