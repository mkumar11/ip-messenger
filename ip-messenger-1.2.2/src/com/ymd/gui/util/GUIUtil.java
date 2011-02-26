package com.ymd.gui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.util.Constants;

/**
 * Simple GUI utilities class.
 * 
 * @author yaragalla Muralidhar.
 *  
 */
public class GUIUtil {
	
	private static IPMLogger logger=IPMLogger.getLogger();
	
	private static int currentX;
	private static int currentY;
	
	/**
	 * creates a non decorated JFrame to display simple messages.
	 * @param x - x position on screen.
	 * @param y - y position on screen.
	 * @param msg - message that is to be displayed.
	 * @return JFrame.
	 */
	public static JFrame displayMessage(int x,int y,String msg){
		JFrame frame=new JFrame();
		JDesktopPane dp=new JDesktopPane();
		dp.setLayout(new BorderLayout());
		
		JTextArea jft=new JTextArea(msg);
		jft.setEditable(false);
		jft.setBackground(Color.WHITE);
		dp.add(jft,BorderLayout.CENTER);
		
		frame.setContentPane(dp);
		frame.setUndecorated(true);
		frame.setLocation((x+50), (y+100));
		frame.setVisible(true);
		frame.toFront();
		frame.pack();
		return frame;
	}	
	
	
	/**
	 * Given component width and height it returns cords which 
	 * can set the component at the mid of desktop.
	 *  
	 * @param width
	 * @param height
	 * @return
	 */
	public static CompCenterCords getCompCenterCords(int width,int height){
		CompCenterCords cords=new CompCenterCords();
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension dim=toolkit.getScreenSize();
		int screenHeight=dim.height;
		int screenWidth=dim.width;
		int heightMidPoint=screenHeight/2;
		int widthMidPoint=screenWidth/2;
		
		int compX=widthMidPoint-(width/2);
		int finalCompX=compX+currentX;
		
		int compY=heightMidPoint-(height/2);
		int finalCompY=compY-currentY;		
		
		if(finalCompY==0 ||finalCompY<0){
			currentY=0;
			currentX=currentX+30;
			finalCompY=compY;
			finalCompX=finalCompX+currentX;
		}
		if((finalCompX+width)==screenWidth ||(finalCompX+width)>screenWidth){
			currentX=0;
			finalCompX=compX;
		}
		cords.setX(finalCompX);
		cords.setY(finalCompY);
		currentY=currentY+30;
		return cords;
	}
	
	/**
	 * This creates a dialog which gives option to choose the
	 * directory for the given property.
	 * 
	 * @param propkey - property name.
	 * @param fileUrl - properties file url.
	 * @param title - title of the dialog.
	 * @param owner - owner of the dialog.
	 */
	public static void createDirDialog(final String propkey,final String filePath,String title,Frame owner){
		
		final JDialog jd=new JDialog(owner,title,true);		
		Container container=jd.getContentPane();
		container.setLayout(null);
		final JLabel errorLabel=new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setBounds(20, 5, 300, 20);
		final JTextField pathTextField=new JTextField();		
		String value=System.getProperty(propkey);	
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
			      File selectedFile=dirChooser.getSelectedFile();
			      String selectedPath=selectedFile.getAbsolutePath();	
			      pathTextField.setText(selectedPath);
			      if(!selectedFile.canWrite()){
			    	  errorLabel.setText(IPMessenger.resources.getString("noWriteAccess"));			    	  
			      }else{
			    	  errorLabel.setText("");
			    	  System.setProperty(propkey, selectedPath);
			      }
			     
			    }
			}
		});
		browse.setBounds(230, 30, 100, 30);
		JButton okButton=new JButton(IPMessenger.resources.getString("optionsDialogeOk"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){							
				try{
					if(errorLabel.getText().isEmpty()){
						saveProperties(filePath);					
						jd.dispose();
					}
				}catch(IOException ioe){
					logger.error(ioe.getMessage(),ioe);
				}
				
			}
		});
		okButton.setBounds(110, 70, 100, 30);
		container.add(errorLabel);
		container.add(pathTextField);
		container.add(browse);
		container.add(okButton);
		jd.setLocationRelativeTo(owner);
		jd.setSize(350, 150);		
		jd.setVisible(true);		
	}
	
	/**
	 * This saves the Properties to the given file path.
	 * 
	 * @param absoluteFilePath - complete file name.
	 * @throws IOException
	 */
	public static void saveProperties(String absoluteFilePath)throws IOException{
		Properties props=new Properties();
		String downloadDir=System.getProperty(Constants.DOWNLOAD_FILE_DIR_KEY);
		props.setProperty(Constants.DOWNLOAD_FILE_DIR_KEY, downloadDir);
		String logDir=System.getProperty(Constants.LOG_FILE_DIR_KEY);
		props.setProperty(Constants.LOG_FILE_DIR_KEY, logDir);
		String chatDir=System.getProperty(Constants.CHAT_FILE_DIR_KEY);
		props.setProperty(Constants.CHAT_FILE_DIR_KEY, chatDir);
		File propFile=new File(absoluteFilePath);
		FileWriter confWriter=new FileWriter(propFile);
		props.store(confWriter, null);
		confWriter.flush();
		confWriter.close();
	}
	
	/**
	 * It holds desktop center cords.
	 * 
	 * @author Muralidhar Yaragalla.
	 *
	 */
	public static class CompCenterCords{
		private int x;
		private int y;
		
		
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}		
	}
}
