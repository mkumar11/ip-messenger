package com.ymd.net.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.ChatGui;
import com.ymd.gui.MainGui;
import com.ymd.gui.util.GUIUtil;
import com.ymd.util.GUID;

/**
 * This is the thread which runs the chat client 
 * window.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class ChatClient implements Runnable{	
	
	private String ip;
	private MainGui mainGui;
	
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
		Socket socket=null;
		InputStream in=null;
		OutputStream out=null;
		try{
			JFrame statusFrame=GUIUtil.displayMessage(mainGui.getX(), mainGui.getY(),
					"Establishing Connection. Please Wait...");
			socket=new Socket(ip,1986);						
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			//sending the same id to the destination IP.
			String chatId=GUID.generateId();			
			out.write(chatId.getBytes());
			out.write(-2);
			
			//sending the user name.
			String user=System.getProperty("user.name");
			out.write(user.getBytes());
			out.write(-2);
			
			statusFrame.dispose();
			ChatGui chat=new ChatGui(socket);			
			chat.setId(chatId);
			
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			
			StringBuffer msg=new StringBuffer();
			StringBuffer userName=new StringBuffer();
			
			boolean userNameValue=true;
			boolean msgValue=false;
			
			while(true){
				int value=in.read();	
				
				if(value==-1){
					chat.dispose();
					break;
				}
				
				if(((byte)value) !=-1){
					chat.setRemoteUserClosed(true);
				}
				
				if(msgValue){
					if(((byte)value) !=-2){
						char ch=(char)value;
						msg.append(ch);
					}else{
						try{
							if(!chat.isVisible())
								chat.setVisible(true);
							doc.insertString(doc.getLength(), chat.getRemoteUserName()+" : ",bold);
							doc.insertString(doc.getLength(), msg.toString()+"\n",null);
							mainArea.setCaretPosition(doc.getLength());
							chat.setExtendedState(0);
							chat.toFront();
						}catch(BadLocationException ble){
							ble.printStackTrace();
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
			System.out.println("Thread gracefully closed.");
		}finally{
			try{				
				in.close();
				out.close();
				socket.close();	
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
	}
	
	

}
