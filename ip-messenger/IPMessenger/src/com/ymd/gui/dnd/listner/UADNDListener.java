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
import com.ymd.net.ft.FileClient;

/**
 * @author yaragamu
 *
 */
public class UADNDListener extends DropTargetAdapter{
	
	private String ip;
	private ChatGui chatGui;
	
	/**
	 * constructs UADNDListener object.
	 * 
	 * @param ip - destination IP address.
	 */
	public UADNDListener(String ip,ChatGui chatGui){
		this.ip=ip;
		this.chatGui=chatGui;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent event) {
		StyledDocument doc=(StyledDocument)chatGui.getMa().getDocument();
		SimpleAttributeSet bold=new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		try {
			Transferable transferable = event.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				List<File> fileList = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = iterator.next();
					String simpleName=file.getName();
					try{
						doc.insertString(doc.getLength(), "Me : ",bold);
						doc.insertString(doc.getLength(), simpleName+"\n",null);					
						JPanel jp=new JPanel(new BorderLayout());				
						JTextField jtf=new JTextField("Awaiting For File Transfer acceptance...");
						jp.add(jtf,BorderLayout.CENTER);
						Style style = doc.addStyle("StyleName", null);
					    StyleConstants.setComponent(style, jp);
					    doc.insertString(doc.getLength(), "ignored text"+"\n", style);					
					}catch(BadLocationException ble){
						ble.printStackTrace();
					}
					Thread fileClient=new Thread(new FileClient(ip,file));
					fileClient.start();					
				}
				event.getDropTargetContext().dropComplete(true);
			} else if (transferable.isDataFlavorSupported (DataFlavor.stringFlavor)){
				event.acceptDrop(DnDConstants.ACTION_COPY);
				String s = (String)transferable.getTransferData ( DataFlavor.stringFlavor);
				System.out.println(s);
				event.getDropTargetContext().dropComplete(true);
			}else{
				event.rejectDrop();
			}
		}catch (IOException exception) {
			exception.printStackTrace();			
			event.rejectDrop();
		}catch (UnsupportedFlavorException ufException ) {
			ufException.printStackTrace();			
			event.rejectDrop();
		}		
	}

	
}
