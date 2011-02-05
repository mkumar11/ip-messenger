package com.ymd.net.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
			InetAddress recipentAddr=socket.getInetAddress();			
			String recipentName=recipentAddr.getHostName();
			in=socket.getInputStream();
			out=socket.getOutputStream();
			String chatId=GUID.generateId();
			
			//sending the same id to the destination IP.
			out.write(chatId.getBytes());
			out.write(-2);
			
			statusFrame.dispose();
			ChatGui chat=new ChatGui(recipentAddr,out);			
			chat.setId(chatId);
			chat.setVisible(true);
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			StringBuffer msg=new StringBuffer();
			while(true){
				int value=in.read();	
				
				if(value==-1)
					break;
				
				if(((byte)value) !=-2){
					char ch=(char)value;
					msg.append(ch);
				}else{
					try{
						doc.insertString(doc.getLength(), recipentName+" : ",bold);
						doc.insertString(doc.getLength(), msg.toString()+"\n",null);
						mainArea.setCaretPosition(doc.getLength());
					}catch(BadLocationException ble){
						System.out.println(ble);
					}					
					msg=new StringBuffer();
				}
				
			}
		}catch(Exception ioe){
			ioe.printStackTrace();
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
