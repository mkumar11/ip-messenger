package com.ymd.main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ymd.gui.ChatGui;
import com.ymd.gui.MainGui;
import com.ymd.gui.listner.ExitActionListener;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.JLogoFrame;
import com.ymd.gui.util.GUIUtil.CompCenterCords;
import com.ymd.images.Resource;
import com.ymd.net.Packets;
import com.ymd.net.chat.ChatServer;
import com.ymd.net.ft.FileServer;
import com.ymd.util.NetUtil;
import com.ymd.util.Util;


/**
 * This is the main class for IPMessenger.
 * 
 * @author Muralidhar Yaragalla
 * 
 */
public class IPMessenger {
	
	/**
	 * Holds all the ChatGui Instances.
	 */
	public static final Map<String,ChatGui> chatGuiMap=new HashMap<String,ChatGui>();
	public static final URL iconUrl=Resource.class.getResource("icon.jpg");
	public static ResourceBundle resources;
	
	//static block which registers the cross look and feel.
	static{
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			resources = ResourceBundle.getBundle("resources.IPMessenger", 
                    Locale.getDefault());
		}catch(UnsupportedLookAndFeelException ulfe){
			ulfe.printStackTrace();
		}catch(IllegalAccessException ie){
			ie.printStackTrace();
		}catch(InstantiationException instEx){
			instEx.printStackTrace();
		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}catch (MissingResourceException mre) {
            System.err.println("resources/IPMessenger.properties not found");
            System.exit(1);
        }
	}

	/**
	 * This is the Main method.
	 * @param args
	 */
	public static void main(String[] args) {		
		try{			
			intialCheck();			
			Map<String,DefaultMutableTreeNode> nodeMap=new HashMap<String,DefaultMutableTreeNode>();
			Thread chatServer=new Thread(new ChatServer());
			chatServer.start();
			Thread fileServer=new Thread(new FileServer());
			fileServer.start();
			MulticastSocket multicastSoc=new MulticastSocket(1988); 
			InetAddress group = InetAddress.getByName("225.5.6.4");
			multicastSoc.joinGroup(group);
			MainGui gi=new MainGui("IPMessenger",multicastSoc,group);
			registerInSystemTray(gi,multicastSoc,group);
			JTree mainTree=gi.getMainTree();
			DefaultTreeModel treeModel=(DefaultTreeModel)mainTree.getModel();
			DefaultMutableTreeNode top=gi.getTop();					
			
			Packets.fireHelloPacket(multicastSoc,group);
			while(true){
				byte[] buf = new byte[26];
				 DatagramPacket recv = new DatagramPacket(buf, buf.length);
				 multicastSoc.receive(recv);
				 
				 StringBuffer temp=new StringBuffer();
				 for(int i=0;i<buf.length;i++){
					 char chr=(char)buf[i];
					 temp.append(chr);					
				 }
				 String pack=temp.toString();
				 String[] tok=pack.split("&");	
				 InetAddress pacSenderAddr=recv.getAddress();
				 if(tok[0].equalsIgnoreCase("hello")){					 
					 String ip=pacSenderAddr.getHostAddress();			 
					 if(!Util.localHost(ip)){ 
						 Packets.fireHandShakePacket(multicastSoc,group);
						 if(!nodeMap.containsKey(ip)){
							 String name=pacSenderAddr.getHostName();
							 String user=Util.removePadding(tok[1]);						 
							 int existingNodes=top.getChildCount();
							 
							 DefaultMutableTreeNode userNode=new DefaultMutableTreeNode(user);
							 DefaultMutableTreeNode ipNode=new DefaultMutableTreeNode(ip);							 
							 DefaultMutableTreeNode nameNode=new DefaultMutableTreeNode(name);
							 							 
							 userNode.add(ipNode);
							 userNode.add(nameNode);
							 
							 nodeMap.put(ip, userNode);
							 
							 top.add(userNode);
							 
							 mainTree.expandRow(0);
							 int index[]={existingNodes};
							 treeModel.nodesWereInserted(top,index );
							 
						 }
						
					 
					 }
				 }
				 
				 if(tok[0].equalsIgnoreCase("hands")){
					 String ip=pacSenderAddr.getHostAddress();
					 if(!Util.localHost(ip)){
						 if(!nodeMap.containsKey(ip)){
							 String name=pacSenderAddr.getHostName();
							 String user=Util.removePadding(tok[1]);						 
							 int existingNodes=top.getChildCount();
							 
							 DefaultMutableTreeNode userNode=new DefaultMutableTreeNode(user);
							 DefaultMutableTreeNode ipNode=new DefaultMutableTreeNode(ip);							 
							 DefaultMutableTreeNode nameNode=new DefaultMutableTreeNode(name);
							 							 
							 userNode.add(ipNode);
							 userNode.add(nameNode);
							 
							 nodeMap.put(ip, userNode);
							 
							 top.add(userNode);
							 
							 mainTree.expandRow(0);
							 int index[]={existingNodes};
							 treeModel.nodesWereInserted(top,index );
							 
						 }
					 }
				 }
				 
				 if(tok[0].equalsIgnoreCase("goodb")){
					 String ip=pacSenderAddr.getHostAddress();
					 if(!Util.localHost(ip)){
						 DefaultMutableTreeNode node=nodeMap.get(ip); 
						 if(node != null){
							 int index=top.getIndex(node);
							 int indexArr[]={index};							 
							 top.remove(node);
							 Object removedChildren[]={node};
							 treeModel.nodesWereRemoved(top, indexArr, removedChildren);
							 nodeMap.remove(ip);
						 }
					 }
				 }
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	/**
	 * It displays the IPMessenger logo and does initial check required.
	 */
	private static void intialCheck(){
		URL url=Resource.class.getResource("logo.jpg");
		JLogoFrame logoFrame=new JLogoFrame(url);
		JProgressBar progress=logoFrame.getProgressBar();
		progress.setMaximum(3);
		boolean mainPort=NetUtil.isPortAvailable(1988);
		progress.setValue(1);
		boolean chatPort=NetUtil.isPortAvailable(1986);
		progress.setValue(2);
		boolean filePort=NetUtil.isPortAvailable(1984);
		progress.setValue(3);
		logoFrame.dispose();
		if(!(mainPort && chatPort && filePort)){
			JFrame errorFrame=GUIUtil.displayMessage(0, 0, "IPMessenger Error Mesage : " +
					"Required Ports On The System Are Not Free. " +
					"Exiting Application.\n\n");			
			Dimension dim=errorFrame.getSize();
			CompCenterCords cords=GUIUtil.getCompCenterCords(dim.width,dim.height);
			errorFrame.setLocation(cords.getX(), cords.getY());
			try{
				Thread.sleep(10000);
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}
			errorFrame.dispose();
			System.exit(0);
		}
	}
	
	/**
	 * Registers the application in the system tray.
	 * 
	 * @param mainGui - MainGui.
	 * @param multicastSoc - MulticastSocket.
	 * @param group - Broadcasting group IP.
	 */
	private static void registerInSystemTray(final MainGui mainGui,MulticastSocket multicastSoc,InetAddress group){
		ImageIcon icon=new ImageIcon(iconUrl);
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image =icon.getImage();
			PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ExitActionListener(multicastSoc,group));
			popup.add(exitItem);
			TrayIcon trayIcon=new TrayIcon(image,"IPMessenger",popup);
			trayIcon.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if(e.getClickCount()==2){
						mainGui.setVisible(true);
					}
				}
			});
			try {
	             tray.add(trayIcon);
	         } catch (AWTException e) {
	             e.printStackTrace();
	         }
		}
		
	}
}
