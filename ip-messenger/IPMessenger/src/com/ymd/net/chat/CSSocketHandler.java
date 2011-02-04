package com.ymd.net.chat;

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
public class CSSocketHandler implements Runnable{
	
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
		try{			
			is=socket.getInputStream();			
			out=socket.getOutputStream();	
			InetAddress clientAddr=socket.getInetAddress();			
			String clientName=clientAddr.getHostName();
			ChatGui chat=new ChatGui(clientAddr,out);
			JTextPane mainArea=chat.getMa();
			Document doc=mainArea.getDocument();			
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			
			StringBuffer msg=new StringBuffer();
			StringBuffer id=new StringBuffer();
			
			boolean idValue=true;
			boolean msgValue=false;
			
			while(true){									
				int value=is.read();	
				
				if(value==-1)
					break;
				
				if(msgValue){
					if(((byte)value) !=-2){
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
				
				// collects the ID bytes.
				if(idValue){
					if(((byte)value)==-2){
						chat.setId(id.toString());
						chat.setVisible(true);
						idValue=false;
						msgValue=true;
					}else{
						char chr=(char)value;
						id.append(chr);
					}
				}
			}		
			
			
			
			
		}catch(IOException ioe){
			System.out.println(ioe);
		}finally{
			try{
				is.close();
				out.close();
				socket.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}	
}
