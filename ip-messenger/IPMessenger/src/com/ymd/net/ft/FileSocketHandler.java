/**
 * 
 */
package com.ymd.net.ft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ymd.gui.ChatGui;
import com.ymd.gui.listner.FTDListener;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.util.Constants;
import com.ymd.util.FileUtil;

/**
 * This handles the File Server sockets.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class FileSocketHandler implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private Socket fileSocket;
	private ChatGui associatedChatGui;
	private StatusPanels panels;
	
	/**
	 * Constructs the FileSocketHadler object.
	 * 
	 * @param fileSocket - The socket it has to handle.
	 */
	public FileSocketHandler(Socket fileSocket){
		this.fileSocket=fileSocket;
	}

	@Override
	public void run() {
		InputStream fsInputStream=null;
		OutputStream fsOutputStream=null;
		try{
			fsInputStream=fileSocket.getInputStream();
			fsOutputStream=fileSocket.getOutputStream();
			File file=null;			
			FileOutputStream fos=null;			
			StringBuffer fileName=new StringBuffer();
			StringBuffer chatId=new StringBuffer();
			StringBuffer fileSize=new StringBuffer();			
			
			boolean fileNameChar=false;
			boolean fileValue=false;
			boolean chatIdChar=true;
			boolean fileSizeChar=false;
			
			Thread blinkThread=null;
			
			JProgressBar jpb=null;
			int count=1;
			
			while(true){
				
				int value=fsInputStream.read();
				
				if(value==-1){	
					if(fos != null){
						fos.flush();
						fos.close();
					}
					if(jpb != null && jpb.getMaximum()== jpb.getValue()){
						JPanel statusPanel=panels.getStatus();
						Component[] spComps=statusPanel.getComponents();
						JTextField statusFld=(JTextField)spComps[0];					
						statusFld.setText(IPMessenger.resources.getString("completedFT"));
					}
					break;
				}
				
				if(fileValue){						
					fos.write(value);
					jpb.setValue(count);
					count=count+1;
				}
				
				//collection of file size bytes
				if(fileSizeChar){
					if(((byte)value) != -2){						
						char ch=(char)value;
						fileSize.append(ch);
					}else{
						JPanel progressPanel=panels.getProgress();
						Component[] ppComps=progressPanel.getComponents();
						jpb=(JProgressBar)ppComps[0];						
						Long fileSizeVal=new Long(fileSize.toString());
						int totalSize=fileSizeVal.intValue();
						jpb.setMaximum(totalSize);
						fos=new FileOutputStream(file);
						fileSizeChar=false;
						fileValue=true;
					}
				}
				
				//collects the file name bytes.
				if(fileNameChar){
					if(((byte)value) != -2){                                                
                        char ch=(char)value;
                        fileName.append(ch);
					}else{
                        if(!associatedChatGui.isVisible())
                                associatedChatGui.setVisible(true);
                        String dir=System.getProperty(Constants.DOWNLOAD_FILE_DIR_KEY);
                        file=FileUtil.createNonExistingFile(fileName.toString(),dir);
                        panels=dispalyFTIntializationMsg(fileName.toString(),fsOutputStream);
                        associatedChatGui.getMa().setCaretPosition(associatedChatGui.getMa()
                        		.getDocument().getLength()); 
                        if(!associatedChatGui.isFocused()){
							if(blinkThread==null ||(!blinkThread.isAlive())){
								ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
								blinkThread=new Thread(new GuiIconBlink(associatedChatGui,IPMessenger.blinkImages,icon.getImage()));
								blinkThread.start();
							}
						}
                        fileNameChar=false;
                        fileSizeChar=true;
					}
				}
				
				//collects the chatId chars.
				if(chatIdChar){
					if(((byte)value) != -2){
						char chr=(char)value;
						chatId.append(chr);
					}else{							
						associatedChatGui=IPMessenger.chatGuiMap.get(chatId.toString());						
						fileNameChar=true;
						chatIdChar=false;
					}
				}
			}
			
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);			
		}finally{
			try{
				fsOutputStream.close();
				fsInputStream.close();
				fileSocket.close();				
			}catch(IOException ioe){
				logger.error(ioe.getMessage(), ioe);
			}
		}		
	}	
	
	
	/**
	 * This method displays status panels in the associated chat Gui.
	 * 
	 * @param fileSimpleName - associated file name.
	 * @param fsOutputStream - associated socket OutputStream.
	 * @return StatusPanels.
	 */
	private StatusPanels dispalyFTIntializationMsg(String fileSimpleName,OutputStream fsOutputStream){	
		StatusPanels panels=new StatusPanels();
		SimpleAttributeSet bold=new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		try{			
			StyledDocument doc=(StyledDocument)associatedChatGui.getMa().getDocument();
			doc.insertString(doc.getLength(), associatedChatGui.getRemoteUserName()+" : ",bold);
			doc.insertString(doc.getLength(), fileSimpleName+"\n",null);					
			JPanel statusPanel=new JPanel(new BorderLayout());				
			JTextField jtf=new JTextField(IPMessenger.resources.getString("initializedFT"));
			jtf.setEditable(false);
			statusPanel.add(jtf,BorderLayout.CENTER);
			Style style = doc.addStyle("StyleName", null);
		    StyleConstants.setComponent(style, statusPanel);
		    doc.insertString(doc.getLength(), "File Transfer"+"\n", style);	
		    panels.setStatus(statusPanel);
		    
		    JPanel decissionPanel=new JPanel(new GridLayout(1,2,10,10));
		    FTDListener ftdLstener=new FTDListener(statusPanel,decissionPanel,fsOutputStream);
		    JButton accept=new JButton(IPMessenger.resources.getString("ftdPannelAccept"));	
		    accept.setActionCommand("accept");
		    accept.addActionListener(ftdLstener);
		    JButton reject=new JButton(IPMessenger.resources.getString("ftdPannelReject"));
		    reject.setActionCommand("reject");
		    reject.addActionListener(ftdLstener);
		    decissionPanel.add(accept);
		    decissionPanel.add(reject);
		    Style styleDp = doc.addStyle("StyleName", null);
		    StyleConstants.setComponent(styleDp, decissionPanel);
		    doc.insertString(doc.getLength(), "File Transfer"+"\n", styleDp);
		    panels.setProgress(decissionPanel);
		    
		}catch(BadLocationException ble){
			logger.error(ble.getMessage(), ble);
		}
		return panels;
	}
	
	/**
	 * Inner class used to holds the Status Panels.
	 */
	public static class StatusPanels{
		
		private JPanel status;
		private JPanel progress;
		
		
		public JPanel getStatus() {
			return status;
		}
		public void setStatus(JPanel status) {
			this.status = status;
		}
		public JPanel getProgress() {
			return progress;
		}
		public void setProgress(JPanel progress) {
			this.progress = progress;
		}		
	}
}
