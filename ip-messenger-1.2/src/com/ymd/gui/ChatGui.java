package com.ymd.gui;

import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.ymd.gui.dnd.listner.UADNDListener;
import com.ymd.gui.listner.ChatGUIUAListener;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GUIUtil.CompCenterCords;
import com.ymd.main.IPMessenger;

/**
 * This is the GUI class for chat window.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class ChatGui extends JFrame {
	
	private static final long serialVersionUID = 4942132120249803370L;
	
	/**
	 * Main Area associated with this chat window.
	 */
	private JTextPane ma;
	
	/**
	 * User area associated with this chat window.
	 */
	private JTextArea ua;
	
	/**
	 * Unique id for this chat window.
	 */
	private String id;
	
	/**
	 * OutputStream associated with this chat window.
	 */
	private OutputStream out;
	
	/**
	 * User Name with whom the chat is happening.
	 */
	private String remoteUserName;
	
	/**
	 * Remote user chat window status.
	 */
	private boolean remoteUserClosed;
	
	
	/**
	 * Constructor which configures the main properties 
	 * and creates the required GUI.
	 * @param title
	 * @param out
	 */
	public ChatGui(final Socket socket){		
		try{
			out=socket.getOutputStream();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}		
		JDesktopPane dp=new JDesktopPane();
		dp.setLayout(null);
		this.setContentPane(dp);
		final ChatGui thisChat=this;
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try{
					out.write(-1);
				}catch(IOException ioe){
					ioe.printStackTrace();
				}
				if(remoteUserClosed){
					thisChat.dispose();
					try{
						out.close();
						socket.close();
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
					IPMessenger.chatGuiMap.remove(id);	
				}
			}
		});
		
		ma=new JTextPane();
		ma.setEditable(false);
		JScrollPane jspma=new JScrollPane(ma);		
		jspma.setBounds(0, 0, 250, 300);
		dp.add(jspma);
		
		ua=new JTextArea();
		ua.setToolTipText("Enter your chat message here...");
		ua.setDragEnabled(true);
		ua.setLineWrap(true);
		ua.setWrapStyleWord(true);
		ua.addKeyListener(new ChatGUIUAListener(this));
		new DropTarget(ua,new UADNDListener(socket.getInetAddress().getHostAddress(),this));
		JScrollPane jspua=new JScrollPane(ua);		
		jspua.setBounds(0, 300, 250, 100);		
		dp.add(jspua);		
		
		ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
		setIconImage(icon.getImage());				
		setSize(260, 450);		
		CompCenterCords cords=GUIUtil.getCompCenterCords(260, 450);
		setLocation(cords.getX(), cords.getY());
	}
	
	
	

	public JTextPane getMa() {
		return ma;
	}

	public JTextArea getUa() {
		return ua;
	}	

	public OutputStream getOut() {
		return out;
	}




	public String getId() {
		return id;
	}



	/**
	 * This sets the id field and also adds this 
	 * instance to the main chat GUI map.
	 * 
	 * @param id - identifier of this instance.
	 */
	public void setId(String id) {
		IPMessenger.chatGuiMap.put(id, this);
		this.id = id;
	}

	public String getRemoteUserName() {
		return remoteUserName;
	}




	public void setRemoteUserName(String remoteUserName) {
		this.remoteUserName = remoteUserName;
	}




	public boolean isRemoteUserClosed() {
		return remoteUserClosed;
	}




	public void setRemoteUserClosed(boolean remoteUserClosed) {
		this.remoteUserClosed = remoteUserClosed;
	}
		
}
