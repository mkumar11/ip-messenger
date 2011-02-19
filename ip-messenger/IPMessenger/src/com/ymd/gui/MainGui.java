package com.ymd.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ymd.gui.listner.ExitActionListener;
import com.ymd.gui.listner.JTreeMouseListener;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GUIUtil.CompCenterCords;
import com.ymd.main.IPMessenger;
import com.ymd.util.Constants;

/**
 * This is the main GUI window.
 * 
 * @author yaragalla Muralidhar
 *  
 */
public class MainGui extends JFrame{

	private static final long serialVersionUID = -4819025906370311549L;	
	

	private DefaultMutableTreeNode top;
	private JTree mainTree;
	
	/**
	 * This constructor configures and creates the main 
	 * GUI Interface for the chat application.
	 * 
	 * @param title - title of this instance.
	 * @param multicastSoc - MulticastSocket.
	 * @param group - Broadcasting group IP.
	 */
	public MainGui(String title,MulticastSocket multicastSoc,InetAddress group){
		super(title);
		
		JDesktopPane dp=new JDesktopPane();		
		dp.setLayout(new BorderLayout());
		JMenuBar menuBar=new JMenuBar();
		/*JMenu call=new JMenu("Call");
		JMenuItem newCall=new JMenuItem("Place Call");
		call.add(newCall);
		JMenuItem exit=new JMenuItem("Exit");
		exit.addActionListener(new ExitActionListener(multicastSoc,group));
		call.add(exit);
		menuBar.add(call);*/
		
		
		top =new DefaultMutableTreeNode(IPMessenger.resources.getString("mainGuiOnlineSystems"));		
		mainTree=new JTree(top);		
		mainTree.addMouseListener(new JTreeMouseListener(this));
		JScrollPane mtsp=new JScrollPane(mainTree);
		dp.add(mtsp,BorderLayout.CENTER);
		setContentPane(dp);			
		ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
		setIconImage(icon.getImage());				
		setSize(200, 550);
		CompCenterCords cords=GUIUtil.getCompCenterCords(200, 550);
		setLocation(cords.getX(), cords.getY());
		
		JMenu exitMenu=new JMenu(IPMessenger.resources.getString("exit"));
		JMenuItem exit=new JMenuItem(IPMessenger.resources.getString("exit"));
		exit.addActionListener(new ExitActionListener(multicastSoc,group));
		exitMenu.add(exit);
		menuBar.add(exitMenu);
		
		final MainGui mainGui=this;
		JMenu settings=new JMenu(IPMessenger.resources.getString("settings"));
		JMenuItem downloaddir=new JMenuItem(IPMessenger.resources.getString("downloadDirectory"));
		JMenuItem logdir=new JMenuItem(IPMessenger.resources.getString("chooseLogFileDirectory"));
		JMenuItem chatdir=new JMenuItem(IPMessenger.resources.getString("chooseChatFileDir"));
		downloaddir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){				
				String filePath=IPMessenger.confFilePath;
				GUIUtil.createDirDialog(Constants.DOWNLOAD_FILE_DIR_KEY, filePath,
						IPMessenger.resources.getString("downloadDirectory"), mainGui);
			}
		});
		
		logdir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){				
				String filePath=IPMessenger.confFilePath;
				GUIUtil.createDirDialog(Constants.LOG_FILE_DIR_KEY, filePath,
						IPMessenger.resources.getString("chooseLogFileDirectory"), mainGui);
			}
		});
		
		chatdir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){				
				String filePath=IPMessenger.confFilePath;
				GUIUtil.createDirDialog(Constants.CHAT_FILE_DIR_KEY, filePath,
						IPMessenger.resources.getString("chooseChatFileDir"), mainGui);
			}
		});
		settings.add(downloaddir);
		settings.add(logdir);
		settings.add(chatdir);
		menuBar.add(settings);
		
		
		
		// this needs cords so call after setting the location.
		JMenu help=new JMenu(IPMessenger.resources.getString("help"));
		JMenuItem about=new JMenuItem(IPMessenger.resources.getString("about"));		
		final int x=getX();		
		final int y=getY();
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				About about=new About(IPMessenger.resources.getString("about"),x,y);
				about.setVisible(true);
			}
		});
		help.add(about);
		menuBar.add(help);
		
		setJMenuBar(menuBar);		
		setVisible(true);
	}
	
	

	public DefaultMutableTreeNode getTop() {
		return top;
	}

	public JTree getMainTree() {
		return mainTree;
	}
	
	/**
	 * This is a frame which displays info about IPMessenger.
	 * 
	 * @author Muralidhar Yaragalla.
	 *
	 */
	private static class About extends JFrame{
		
		private static final long serialVersionUID = 247384236233249085L;

		public About(String title,int x,int y){
			super(title);
			Container container=getContentPane();
			container.setLayout(new BorderLayout());
			JTextArea jta=new JTextArea();
			jta.setText("\n\nCreator : Muralidhar Yaragalla.\nEmail : " +
					"yaragallamurali@gmail.com\nCode Site : " +
					"http://code.google.com/p/ip-messenger/ \n" +
					"Version : 1.2.1");
			jta.setEditable(false);
			container.add(jta,BorderLayout.CENTER);
			ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
			setIconImage(icon.getImage());	
			setSize(277, 200);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
			setLocation(x, y);			
		}
	}
}
