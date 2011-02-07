package com.ymd.main;

import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ymd.gui.ChatGui;
import com.ymd.gui.MainGui;
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
	
	//static block which registers the cross look and feel.
	static{
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}catch(UnsupportedLookAndFeelException ulfe){
			System.out.println(ulfe);
		}catch(IllegalAccessException ie){
			System.out.println(ie);
		}catch(InstantiationException instEx){
			System.out.println(instEx);
		}catch(ClassNotFoundException cnfe){
			System.out.println(cnfe);
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
			JTree mainTree=gi.getMainTree();
			DefaultTreeModel treeModel=(DefaultTreeModel)mainTree.getModel();
			DefaultMutableTreeNode top=gi.getTop();
			boolean firsttime=true;			
			
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
							 String name="HostName: "+pacSenderAddr.getHostName();
							 String user="UserName: "+Util.removePadding(tok[1]);						 
							 int existingNodes=top.getChildCount();

							 DefaultMutableTreeNode ipNode=new DefaultMutableTreeNode(ip);							 
							 DefaultMutableTreeNode nameNode=new DefaultMutableTreeNode(name);
							 DefaultMutableTreeNode userNode=new DefaultMutableTreeNode(user);							 
							 ipNode.add(nameNode);
							 ipNode.add(userNode);
							 
							 nodeMap.put(ip, ipNode);
							 
							 top.add(ipNode);
							 if(firsttime){
								 mainTree.expandRow(0);
								 firsttime=false;
							 }else{
								 
								 int index[]={existingNodes};
								 treeModel.nodesWereInserted(top,index );
							 }
						 }
						
					 
					 }
				 }
				 
				 if(tok[0].equalsIgnoreCase("hands")){
					 String ip=pacSenderAddr.getHostAddress();
					 if(!Util.localHost(ip)){
						 if(!nodeMap.containsKey(ip)){
							 String name="HostName: "+pacSenderAddr.getHostName();
							 String user="UserName: "+Util.removePadding(tok[1]);						 
							 int existingNodes=top.getChildCount();
							 
							 DefaultMutableTreeNode ipNode=new DefaultMutableTreeNode(ip);							 
							 DefaultMutableTreeNode nameNode=new DefaultMutableTreeNode(name);
							 DefaultMutableTreeNode userNode=new DefaultMutableTreeNode(user);							 
							 ipNode.add(nameNode);
							 ipNode.add(userNode);
							 
							 nodeMap.put(ip, ipNode);
							 
							 top.add(ipNode);
							 if(firsttime){
								 mainTree.expandRow(0);
								 firsttime=false;
							 }else{
								 int index[]={existingNodes};
								 treeModel.nodesWereInserted(top,index );
							 }
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
							 Object removedChildren[]={node};
							 top.remove(node);
							 treeModel.nodesWereRemoved(top, indexArr, removedChildren);
							 nodeMap.remove(ip);
						 }
					 }
				 }
			}
		}catch(IOException ioe){
			System.out.println(ioe);
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
}
