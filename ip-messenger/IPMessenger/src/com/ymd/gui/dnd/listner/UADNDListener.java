/**
 * 
 */
package com.ymd.gui.dnd.listner;

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

import com.ymd.net.ft.FileClient;

/**
 * @author yaragamu
 *
 */
public class UADNDListener extends DropTargetAdapter{
	
	private String ip;
	
	/**
	 * constructs UADNDListener object.
	 * 
	 * @param ip - destination IP address.
	 */
	public UADNDListener(String ip){
		this.ip=ip;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent event) {
		try {
			Transferable transferable = event.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				List<File> fileList = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = iterator.next();
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
