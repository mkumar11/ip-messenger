/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ymd.gui.MainGui;
import com.ymd.gui.chat.ChatGui;
import com.ymd.main.IPMessenger;
import com.ymd.net.ClientHandler;
import com.ymd.net.ClientHandlerRunnable;
import com.ymd.util.Constants;

/**
 * This is the Listener class for Main Gui's JTree component.
 * 
 * @author yaragalla Muralidhar.
 */
public class JTreeMouseListener extends MouseAdapter{
	
	private MainGui mainGui;
	
	/**
	 * Constructs JTreeMouseListener instance.
	 * 
	 * @param mainGui - MainGui.
	 */
	public JTreeMouseListener(MainGui mainGui){
		this.mainGui=mainGui;
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){			
			JTree jtree=(JTree)e.getComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
			String ip=null;			
			if(!node.isRoot()){
				if(node.isLeaf()){
					String nodeName=node.getUserObject().toString();
					String tok[]=nodeName.split("\\.");
					if (tok.length==4){
						ip=nodeName;
					}else{
						DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)node.getParent();
						DefaultMutableTreeNode ipNode=parentNode.getFirstLeaf();
						ip=ipNode.getUserObject().toString();					
					}
				}else{
					DefaultMutableTreeNode ipNode=node.getFirstLeaf();
					ip=ipNode.getUserObject().toString();
					
				}											
				boolean establishConn=true;
				boolean isChatWindowExist=false;			
				if(IPMessenger.ipChatGuiIdMap.containsKey(ip)){
					List<String> chatIds=IPMessenger.ipChatGuiIdMap.get(ip);
					for(String chatId:chatIds){
						isChatWindowExist=true;
						ChatGui chatGui=IPMessenger.chatGuiMap.get(chatId);
						if((!chatGui.isVisible()) || (chatGui.getExtendedState()!=0)){						
							chatGui.setVisible(true);
							chatGui.setExtendedState(0);
							chatGui.toFront();
						}
					}
				}
				
				if(isChatWindowExist){
					int option=JOptionPane.showConfirmDialog(mainGui,
							IPMessenger.resources.getString("alreadyChatWindowExist"));
					if(option != JOptionPane.YES_OPTION)
						establishConn=false;
				}
				
				if(establishConn){
					ClientHandler ch=new ClientHandler(ip);	
					ClientHandlerRunnable chr=new ClientHandlerRunnable(ch,Constants.CHAT);
					Thread clientHandlerThr=new Thread(chr);
					clientHandlerThr.start();	
				}
			}// end of leaf if.
		}
	}
}
