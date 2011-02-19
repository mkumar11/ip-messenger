/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ymd.gui.ChatGui;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;

/**
 * This is the Listener Class for chat window's 
 * user input area.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class ChatGUIUAListener extends KeyAdapter{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private ChatGui chatGui;
	
	/**
	 * Constructs ChatGUIUAListener instance.
	 * 
	 * @param chatGui - ChatGui.
	 */
	public ChatGUIUAListener(ChatGui chatGui){
		this.chatGui=chatGui;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		
		if(event.getKeyCode()==10){	
			event.consume();
			JTextArea userArea=chatGui.getUa();	
			String messageTobeSent=userArea.getText();
			userArea.setText("");			
			JTextPane mainArea=chatGui.getMa();
			Document doc=mainArea.getDocument();
			SimpleAttributeSet bold=new SimpleAttributeSet();
			StyleConstants.setBold(bold, true);
			try{
				doc.insertString(doc.getLength(), IPMessenger.resources.getString("myself")
										+" : ",bold);
				doc.insertString(doc.getLength(), messageTobeSent+"\n",null);
				mainArea.setCaretPosition(doc.getLength());
			}catch(BadLocationException ble){
				logger.error(ble.getMessage(), ble);
			}			
			OutputStream out=chatGui.getOut();
			try{
				if(chatGui.isRemoteUserClosed()){
					chatGui.setRemoteUserClosed(false);					
				}
				out.write(messageTobeSent.getBytes());
				out.write(-2);
			}catch(IOException ioe){
				try{
					String msg=IPMessenger.resources.getString("userNotOnline");
					doc.insertString(doc.getLength(),msg+"\n",bold);
					mainArea.setCaretPosition(doc.getLength());
				}catch(BadLocationException ble){
					logger.error(ble.getMessage(), ble);
				}				
			}			
		}		
	}	
}
