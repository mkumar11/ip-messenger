package com.ymd.gui.chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.ymd.gui.dnd.listner.UADNDListener;
import com.ymd.gui.listner.ChatGUIUAListener;
import com.ymd.gui.listner.ChatSaveListener;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GUIUtil.CompCenterCords;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.Event;
import com.ymd.net.SocketInfo;
import com.ymd.util.Constants;

/**
 * This is the GUI class for chat window.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class ChatGui extends JFrame {
	
	private static final long serialVersionUID = 4942132120249803370L;
	protected IPMLogger logger=IPMLogger.getLogger();
	
	/**
	 * Main Area associated with this chat window.
	 */
	protected JTextPane ma;
	
	/**
	 * User area associated with this chat window.
	 */
	private JTextArea ua;
	
	/**
	 * Unique id for this chat window.
	 */
	protected String id;
	
	
	/**
	 * User Name with whom the chat is happening.
	 */
	protected String remoteUserName;
	
	/**
	 * Remote user chat window status.
	 */
	private boolean remoteUserClosed=true;	
	
	
	/**
	 * This contains socket info.
	 */
	private SocketInfo sockInfo; 	
	
	/**
	 * Default constructor which does nothing.
	 */
	public ChatGui(){
		
	}
	/**
	 * Constructor which configures the main properties 
	 * and creates the required GUI.
	 * @param title
	 * @param out
	 */
	public ChatGui(final SocketInfo sockInfo){
		this.sockInfo=sockInfo;
		Container dp=getContentPane();
		dp.setLayout(new BorderLayout());		
		JSplitPane jsp=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp.setDividerLocation(300);
		final ChatGui thisChat=this;
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try{
					sockInfo.getSocketOOS().writeObject(new Event(Constants.EVENT_WINDOW_CLOSED));
				}catch(IOException ioe){
					logger.error(ioe.getMessage(), ioe);
				}
				if(remoteUserClosed){					
					thisChat.dispose();					
					String ip=sockInfo.getRemoteIP();
					try{
						sockInfo.getSocketOOS().close();
						sockInfo.getSocketOIS().close();						
					}catch(IOException ioe){
						logger.error(ioe.getMessage(),ioe);
					}
					List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(ip);
					chatIds.remove(id);
					IPMessenger.chatGuiMap.remove(id);	
				}
			}
		});
		
		ma=new JTextPane();
		ma.setEditable(false);
		JScrollPane jspma=new JScrollPane(ma);		
		jspma.setBounds(0, 0, 250, 300);
		jsp.add(jspma,JSplitPane.TOP);
		
		
		ua=new JTextArea();
		ua.setToolTipText(IPMessenger.resources.getString("userAreaToolTip"));
		ua.setDragEnabled(true);
		ua.setLineWrap(true);
		ua.setWrapStyleWord(true);
		ua.addKeyListener(new ChatGUIUAListener(this));
		new DropTarget(ua,new UADNDListener(sockInfo.getRemoteIP(),this));
		JScrollPane jspua=new JScrollPane(ua);		
		jspua.setBounds(0, 300, 250, 100);	
		jsp.add(jspua,JSplitPane.BOTTOM);
		
		dp.add(jsp,BorderLayout.CENTER);	
		
		JMenuBar menuBar=new JMenuBar();
		JMenu file=new JMenu(IPMessenger.resources.getString("file"));
		JMenuItem saveChat=new JMenuItem(IPMessenger.resources.getString("saveChat"));
		saveChat.addActionListener(new ChatSaveListener(ma));
		file.add(saveChat);		
		menuBar.add(file);
		
		setJMenuBar(menuBar);
		
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

	
	/**
	 * @return the sockInfo
	 */
	public SocketInfo getSockInfo() {
		return sockInfo;
	}	
		
}
