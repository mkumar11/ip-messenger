/**
 * 
 */
package com.ymd.gui.dnd.listner;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ymd.gui.ChatGui;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.ft.FileClient;

/**
 * Drag and Drop Listener.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class UADNDListener extends DropTargetAdapter{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private String ip;
	private ChatGui chatGui;
	
	/**
	 * constructs UADNDListener object.
	 * 
	 * @param ip - destination IP address.
	 * @param chatGui - chatGui.
	 */
	public UADNDListener(String ip,ChatGui chatGui){
		this.ip=ip;
		this.chatGui=chatGui;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent event) {
		StyledDocument doc=(StyledDocument)chatGui.getMa().getDocument();
		
		try {
			Transferable transferable = event.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				List<File> fileList = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = iterator.next();
					String simpleName=file.getName();
					StatusPanels panels=displayFTStatusMsg(doc,simpleName);
					if(chatGui.isRemoteUserClosed())
						chatGui.setRemoteUserClosed(false);
					chatGui.getMa().setCaretPosition(chatGui.getMa().getDocument().getLength());
					Thread fileClient=new Thread(new FileClient(ip,file,chatGui.getId(),panels));
					fileClient.start();					
				}
				event.getDropTargetContext().dropComplete(true);
			} else if (transferable.isDataFlavorSupported (DataFlavor.stringFlavor)){
				event.acceptDrop(DnDConstants.ACTION_COPY);
				String str = (String)transferable.getTransferData (DataFlavor.stringFlavor);
				chatGui.getUa().append(str);				
				event.getDropTargetContext().dropComplete(true);
			}else{
				event.rejectDrop();
			}
		}catch (IOException exception) {
			logger.error(exception.getMessage(), exception);			
			event.rejectDrop();
		}catch (UnsupportedFlavorException ufException ) {
			logger.error(ufException.getMessage(), ufException);			
			event.rejectDrop();
		}		
	}
	
	/**
	 * This method displays status panels in the associated chat GUI.
	 * 
	 * @param doc - styled document associated with the chat GUI Main Area. 
	 * @param simpleFileName - file name with which this is associated to.
	 * @return StatusPanels
	 */
	private StatusPanels displayFTStatusMsg(StyledDocument doc,String simpleFileName){
		StatusPanels panels=new StatusPanels();
		SimpleAttributeSet bold=new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		try{
			doc.insertString(doc.getLength(), IPMessenger.resources.getString("myself")+" : ",bold);
			doc.insertString(doc.getLength(), simpleFileName+"\n",null);					
			JPanel statusPanel=new JPanel(new BorderLayout());				
			JTextField jtf=new JTextField(IPMessenger.resources.getString("awaitingFTAcceptance"));
			jtf.setEditable(false);
			statusPanel.add(jtf,BorderLayout.CENTER);
			Style style = doc.addStyle("StyleName", null);
		    StyleConstants.setComponent(style, statusPanel);
		    doc.insertString(doc.getLength(), "File Transfer"+"\n", style);	
		    panels.setStatus(statusPanel);
		    
		    JPanel progressPanel=new JPanel(new BorderLayout());			
			Style stylePP = doc.addStyle("StyleName", null);
		    StyleConstants.setComponent(stylePP, progressPanel);
		    doc.insertString(doc.getLength(), "File Transfer"+"\n", stylePP);	
		    panels.setProgress(progressPanel);
		    
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
