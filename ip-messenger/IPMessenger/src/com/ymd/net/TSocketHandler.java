package com.ymd.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.ChatGui;

/**
 * This is the thread which runs the chat server.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class TSocketHandler implements Runnable{
	
	private Socket socket;
	
	public TSocketHandler(Socket socket){
		this.socket=socket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		InputStream is=null;
		OutputStream out=null;
		try{			
			is=socket.getInputStream();			
			out=socket.getOutputStream();	
			InetAddress clientAddr=socket.getInetAddress();
			String clientIp=clientAddr.getHostAddress();
			String clientName=clientAddr.getHostName();
			ChatGui chat=new ChatGui(clientIp+":-"+"IPMessenger",out);
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			StringBuffer msg=new StringBuffer();
			
			while(true){									
				int value=is.read();					
				if(value==255)
					break;
				if(value !=254){
					char ch=(char)value;
					msg.append(ch);
				}else{
					try{
						doc.insertString(doc.getLength(), clientName+" : ",bold);
						doc.insertString(doc.getLength(), msg.toString()+"\n",null);
					}catch(BadLocationException ble){
						System.out.println(ble);
					}					
					msg=new StringBuffer();
				}				
			}		
			
			out.write(-1);
			is.close();
			out.close();
			socket.close();
			
		}catch(IOException ioe){
			System.out.println(ioe);
		}
		
	}

	
}
