/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ymd.gui.MainGui;
import com.ymd.net.chat.ChatClient;

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
			if(node.isLeaf()){
				String nodeName=node.getUserObject().toString();
				String tok[]=nodeName.split("\\.");
				if (tok.length==4){
					Thread client=new Thread(new ChatClient(nodeName,mainGui));
					client.start();
				}else{
					DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)node.getParent();
					DefaultMutableTreeNode ipNode=parentNode.getFirstLeaf();
					String ip=ipNode.getUserObject().toString();
					Thread client=new Thread(new ChatClient(ip,mainGui));
					client.start();
				}
			}else{
				DefaultMutableTreeNode ipNode=node.getFirstLeaf();
				String ip=ipNode.getUserObject().toString();
				Thread client=new Thread(new ChatClient(ip,mainGui));
				client.start();
			}			
		}
	}
}
