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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
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
import com.ymd.log.IPMLogger;
import com.ymd.net.Packets;
import com.ymd.net.chat.ChatServer;
import com.ymd.net.ft.FileServer;
import com.ymd.util.Constants;
import com.ymd.util.NetUtil;
import com.ymd.util.Util;


/**
 * This is the main class for IPMessenger.
 * 
 * @author Muralidhar Yaragalla
 * 
 */
public class IPMessenger {
	
	private static IPMLogger logger;
	
	/**
	 * Holds all the ChatGui Instances.
	 */
	public static final Map<String,ChatGui> chatGuiMap=new HashMap<String,ChatGui>();
	public static final Map<String,List<String>> ipChatGuiIdMap=new HashMap<String,List<String>>();
	public static final URL iconUrl=Resource.class.getResource("icon.jpg");	
	public static final List<Image> blinkImages=new ArrayList<Image>();
	public static ResourceBundle resources;
	public static String confFilePath;
	
	//static block which registers the cross look and feel and does
	//necessary initialization.
	static{
		try{			
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			resources = ResourceBundle.getBundle("com.ymd.main.resources.IPMessenger", 
                    				Locale.getDefault());
			URL urlOne=Resource.class.getResource("one.jpg");
			URL urlTwo=Resource.class.getResource("two.jpg");
			URL urlThree=Resource.class.getResource("three.jpg");
			URL urlFour=Resource.class.getResource("four.jpg");
			
			ImageIcon one=new ImageIcon(urlOne);
			ImageIcon two=new ImageIcon(urlTwo);
			ImageIcon three=new ImageIcon(urlThree);
			ImageIcon four=new ImageIcon(urlFour);
			
			blinkImages.add(one.getImage());
			blinkImages.add(two.getImage());
			blinkImages.add(three.getImage());
			blinkImages.add(four.getImage());
			
			//setting up the required Properties.
			Properties confProps=new Properties();
			String userHomeDir=System.getProperty("user.home");
			File dir=new File(userHomeDir+"\\"+Constants.CONF_DIR);
			if(!dir.exists()){
				dir.mkdir();
			}
			confFilePath=userHomeDir+"\\"+Constants.CONF_DIR+"\\"+Constants.CONF_FILE_NAME;
			File confFile=new File(confFilePath);
			if(!confFile.exists()){
				try{
					confFile.createNewFile();
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
			}
			FileInputStream fis=null;
			try{
				fis=new FileInputStream(confFile);
			}catch(FileNotFoundException fnfe){
				fnfe.printStackTrace();
			}
			try{
				confProps.load(fis);
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			String downloadFileDir=confProps.getProperty(Constants.DOWNLOAD_FILE_DIR_KEY);
			if(downloadFileDir == null || downloadFileDir.isEmpty()){
				downloadFileDir=new File("").getAbsolutePath();
			}
			System.setProperty(Constants.DOWNLOAD_FILE_DIR_KEY, downloadFileDir);
			
			String logFileDir=confProps.getProperty(Constants.LOG_FILE_DIR_KEY);
			if(logFileDir == null || logFileDir.isEmpty()){
				logFileDir=new File("").getAbsolutePath();
			}
			System.setProperty(Constants.LOG_FILE_DIR_KEY, logFileDir);
			logger=IPMLogger.getLogger();
			
			String chatFileDir=confProps.getProperty(Constants.CHAT_FILE_DIR_KEY);
			if(chatFileDir == null || chatFileDir.isEmpty()){
				chatFileDir=new File("").getAbsolutePath();
			}
			System.setProperty(Constants.CHAT_FILE_DIR_KEY, chatFileDir);
			
		}catch(UnsupportedLookAndFeelException ulfe){
			logger.error(ulfe.getMessage(), ulfe);
		}catch(IllegalAccessException ie){
			logger.error(ie.getMessage(),ie);
		}catch(InstantiationException instEx){
			logger.error(instEx.getMessage(), instEx);
		}catch(ClassNotFoundException cnfe){
			logger.error(cnfe.getMessage(), cnfe);
		}catch (MissingResourceException mre) {
			logger.severe("resources/IPMessenger.properties not found");            
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
			MulticastSocket multicastSoc=new MulticastSocket(Constants.MAIN_BROADCASTING_PORT); 
			InetAddress group = InetAddress.getByName(Constants.BROADCASTING_IP);
			multicastSoc.joinGroup(group);
			MainGui gi=new MainGui(IPMessenger.resources.getString("ipmessenger"),
													multicastSoc,group);
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
					 if(!NetUtil.localHost(ip)){ 
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
					 if(!NetUtil.localHost(ip)){
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
					 if(!NetUtil.localHost(ip)){
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
			logger.error(ioe.getMessage(), ioe);
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
			JFrame errorFrame=GUIUtil.displayMessage(0, 0, IPMessenger.resources.getString("portsNotFree"));			
			Dimension dim=errorFrame.getSize();
			CompCenterCords cords=GUIUtil.getCompCenterCords(dim.width,dim.height);
			errorFrame.setLocation(cords.getX(), cords.getY());
			try{
				Thread.sleep(10000);
			}catch(InterruptedException ie){
				logger.error(ie.getMessage(), ie);
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
			MenuItem exitItem = new MenuItem(IPMessenger.resources.getString("exit"));
			exitItem.addActionListener(new ExitActionListener(multicastSoc,group));
			popup.add(exitItem);
			TrayIcon trayIcon=new TrayIcon(image,IPMessenger.resources.getString("ipmessenger"),popup);
			trayIcon.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if(e.getClickCount()==2){
						mainGui.setVisible(true);
					}
				}
			});
			try {
	             tray.add(trayIcon);
	         } catch (AWTException awte) {
	             logger.error(awte.getMessage(), awte);
	         }
		}
		
	}
}
