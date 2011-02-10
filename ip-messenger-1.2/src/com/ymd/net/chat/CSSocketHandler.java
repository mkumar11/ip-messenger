package com.ymd.net.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
			
			ChatGui chat=new ChatGui(socket);
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
								
							}
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
				
				//collecting the User name bytes.
				if(userNameValue){
					if(((byte)value)==-2){
						chat.setRemoteUserName(userName.toString());
						chat.setTitle(userName.toString());
						String user=System.getProperty("user.name");
						out.write(user.getBytes());
						out.write(-2);
						chat.setVisible(true);
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
						chat.setId(id.toString());						
						idValue=false;
						userNameValue=true;
					}else{
						char chr=(char)value;
						id.append(chr);
					}
				}
			}			
		}catch(IOException ioe){
			System.out.println("Thread gracefully closed.");
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
