/**
 * 
 */
package com.ymd.net.ft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ymd.gui.chat.ChatGui;
import com.ymd.gui.util.GuiIconBlink;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.SocketInfo;
import com.ymd.util.Constants;
import com.ymd.util.FileUtil;

/**
 * This handles the File Server sockets.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class ServerFileTransferHandler implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();	
	
	private SocketInfo sockInfo;
	private FileInfo fileInfo;
	private ChatGui associatedChatGui;
	
	/**
	 * Constructs the FileSocketHadler object.
	 * 
	 * @param fileSocket - The socket it has to handle.
	 */
	public ServerFileTransferHandler(SocketInfo sockInfo,FileInfo fileInfo){
		this.sockInfo=sockInfo;
		this.fileInfo=fileInfo;
	}

	@Override
	public void run() {		
		StatusPanels panels;
		BufferedOutputStream bos=null;
		try{
			associatedChatGui=IPMessenger.chatGuiMap.get(fileInfo.getChatId());
			Thread blinkThread=null;
			if(!associatedChatGui.isVisible())
                associatedChatGui.setVisible(true);
	        String dir=System.getProperty(Constants.DOWNLOAD_FILE_DIR_KEY);
	        File file=FileUtil.createNonExistingFile(fileInfo.getFile().getName(),dir);
	        panels=dispalyFTIntializationMsg(fileInfo.getFile().getName());
	        associatedChatGui.getMa().setCaretPosition(associatedChatGui.getMa()
	        		.getDocument().getLength()); 
	        if(!associatedChatGui.isFocused()){
				if(blinkThread==null ||(!blinkThread.isAlive())){
					ImageIcon icon=new ImageIcon(IPMessenger.iconUrl);
					blinkThread=new Thread(new GuiIconBlink(associatedChatGui,IPMessenger.blinkImages,icon.getImage()));
					blinkThread.start();
				}
			}
	        JProgressBar jpb=null;
	        JPanel progressPanel=panels.getProgress();
			Component[] ppComps=progressPanel.getComponents();
			jpb=(JProgressBar)ppComps[0];						
			Long fileSizeVal=fileInfo.getFile().length();
			Long maxRough=fileSizeVal/3000;
			int max=0;
			if(maxRough > 0 && (fileSizeVal%3000) != 0)
				max=maxRough.intValue()+1;
			else
				max=maxRough.intValue();			
			if(max == 0)
				jpb.setMaximum(1);
			else
				jpb.setMaximum(max);
			
			FileOutputStream fos=new FileOutputStream(file);
			bos=new BufferedOutputStream(fos);
			int count=1;
			while(true){
				try{
				Object obj=sockInfo.getSocketOIS().readObject();
				if(obj instanceof FileMultiPart){
					FileMultiPart filePart=(FileMultiPart)obj;
					if(filePart.isLastPart())
						break;
					byte[] partBytes=filePart.getFilePart();
					bos.write(partBytes);
					jpb.setValue(count);
					count=count+1;					
				}
				}catch(ClassNotFoundException cnfe){
					logger.error(cnfe.getMessage(), cnfe);
				}
			}
			JPanel status=panels.getStatus();
			Component[] statusComps=status.getComponents();
			JTextField statustf=(JTextField)statusComps[0];
			statustf.setText(IPMessenger.resources.getString("completedFT"));
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);			
		}finally{
			try{
				bos.flush();
				bos.close();
				sockInfo.getSocketOIS().close();
				sockInfo.getSocketOOS().close();
				sockInfo.getSocket().close();
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
	private StatusPanels dispalyFTIntializationMsg(String fileSimpleName){	
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
		    jtf.setText(IPMessenger.resources.getString("progressFT"));
			JProgressBar jpb=new JProgressBar() ;			
			decissionPanel.setLayout(new BorderLayout());
			decissionPanel.add(jpb, BorderLayout.CENTER);		    
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
