package com.ymd.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ymd.gui.listner.ExitActionListener;
import com.ymd.gui.listner.JTreeMouseListener;
import com.ymd.gui.util.GUIUtil;
import com.ymd.gui.util.GUIUtil.CompCenterCords;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.main.resources.Dummy;

/**
 * This is the main GUI window.
 * 
 * @author yaragalla Muralidhar
 *  
 */
public class MainGui extends JFrame{

	private static final long serialVersionUID = -4819025906370311549L;	
	private IPMLogger logger=IPMLogger.getLogger();

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
		
		JMenu exit=new JMenu(IPMessenger.resources.getString("exit"));
		exit.addActionListener(new ExitActionListener(multicastSoc,group));
		menuBar.add(exit);
		
		JMenu settings=new JMenu(IPMessenger.resources.getString("settings"));
		final JFrame thisFrame=this;
		JMenuItem logPath=new JMenuItem(IPMessenger.resources.getString("logFilePath"));
		logPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				URL conf=Dummy.class.getResource("IPMessengerConf.properties");
				createDirDialog("logFilePath",conf,
						IPMessenger.resources.getString("chooseLogFileDirectory"),
						thisFrame);
			}
		});
		JMenuItem ftPath=new JMenuItem(IPMessenger.resources.getString("fileTransferPath"));
		ftPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				URL conf=Dummy.class.getResource("IPMessengerConf.properties");
				createDirDialog("fileTransferFilePath",
						conf,IPMessenger.resources.getString("chooseFileTransferdirectory"),
						thisFrame);
			}
		});
		settings.add(logPath);
		settings.add(ftPath);
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
	
	
	public void createDirDialog(final String propkey,final URL fileUrl,String title,Frame owner){		
		final JDialog jd=new JDialog(owner,title,true);		
		Container container=jd.getContentPane();
		container.setLayout(null);
		final JTextField pathTextField=new JTextField();
		Properties confProp=null;
		try{
			File confFile=new File(fileUrl.toURI());
			FileInputStream fis=new FileInputStream(confFile);
			confProp=new Properties();
			confProp.load(fis);
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}catch(URISyntaxException se){
			logger.error(se.getMessage(), se);
		}
		String value=confProp.getProperty(propkey);		
		pathTextField.setText(value);
		pathTextField.setEditable(false);
		pathTextField.setBounds(20, 30, 200, 30);
		JButton browse=new JButton(IPMessenger.resources.getString("optionsDialogeBrowse"));		
		browse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JFileChooser dirChooser=new JFileChooser();
				dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);				
				int returnVal = dirChooser.showOpenDialog(jd);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {			       
			      pathTextField.setText(dirChooser.getSelectedFile().getAbsolutePath());			    	
			    }
			}
		});
		browse.setBounds(230, 30, 100, 30);
		JButton okButton=new JButton(IPMessenger.resources.getString("optionsDialogeOk"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){							
				try{
					File confFile=new File(fileUrl.toURI());
					FileWriter confWriter=new FileWriter(confFile);
					Properties confProp=new Properties();
					confProp.setProperty(propkey, pathTextField.getText());					
					confProp.store(confWriter, null);
					jd.dispose();
				}catch(IOException ioe){
					logger.error(ioe.getMessage(),ioe);
				}catch(URISyntaxException se){
					logger.error(se.getMessage(),se);
				}
				
			}
		});
		okButton.setBounds(110, 70, 100, 30);
		container.add(pathTextField);
		container.add(browse);
		container.add(okButton);
		jd.setLocationRelativeTo(this);
		jd.setSize(350, 150);		
		jd.setVisible(true);		
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
					"Version : 1.2");
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
