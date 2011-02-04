package com.ymd.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ymd.gui.ChatGui;
import com.ymd.gui.MainGui;
import com.ymd.net.Packets;
import com.ymd.net.chat.ChatServer;
import com.ymd.net.ft.FileServer;
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
	

	/**
	 * This is the Main method.
	 * @param args
	 */
	public static void main(String[] args) {		
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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
				byte[] buf = new byte[68];
				 DatagramPacket recv = new DatagramPacket(buf, buf.length);
				 multicastSoc.receive(recv);
				 StringBuffer temp=new StringBuffer();
				 for(int i=0;i<buf.length;i++){
					 char chr=(char)buf[i];
					 temp.append(chr);					
				 }
				 String pack=temp.toString();
				 String[] tok=pack.split("&");				 
				 if(tok[0].equalsIgnoreCase("hello")){					 
					 String ip=Util.removePadding(tok[1]);				 
					 if(!Util.localHost(ip)){ 
						 Packets.fireHandShakePacket(multicastSoc,group);
						 if(!nodeMap.containsKey(ip)){
							 String name="HostName: "+Util.removePadding(tok[2]);
							 String user="UserName: "+Util.removePadding(tok[3]);						 
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
					 String ip=Util.removePadding(tok[1]);
					 if(!Util.localHost(ip)){
						 if(!nodeMap.containsKey(ip)){
							 String name="HostName: "+Util.removePadding(tok[2]);
							 String user="UserName: "+Util.removePadding(tok[3]);						 
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
					 String ip=Util.removePadding(tok[1]);
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
}
