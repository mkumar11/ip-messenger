/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ymd.net.ChatClient;

/**
 * This is the Listener class for Main Gui's JTree component.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class JTreeMouseListener extends MouseAdapter{

	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){			
			JTree jtree=(JTree)e.getComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
			String ip=node.getUserObject().toString();
			String tok[]=ip.split("\\.");
			if (tok.length==4){
				Thread client=new Thread(new ChatClient(ip));
				client.start();
			}
		}
	}
}
