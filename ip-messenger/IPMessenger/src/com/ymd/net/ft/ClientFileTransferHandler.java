/**
 * 
 */
package com.ymd.net.ft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import com.ymd.gui.dnd.listner.UADNDListener;
import com.ymd.gui.dnd.listner.UADNDListener.StatusPanels;
import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;
import com.ymd.net.SocketInfo;

/**
 * This establishes connection to the given IP and transfers the file
 * to the destined IP.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class ClientFileTransferHandler implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private SocketInfo sockInfo;
	private FileInfo fileInfo;
	private Document doc;
 
	/**
	 * Constructs the FileClient object.
	 * 
	 * @param ip - IP address of the destination.
	 * @param file - The files which has to be transfered.
	 */
	public ClientFileTransferHandler(SocketInfo sockInfo,FileInfo fileInfo,Document doc){
		this.sockInfo=sockInfo;
		this.fileInfo=fileInfo;
		this.doc=doc;
	}

	@Override
	public void run() {	
		BufferedInputStream bis=null;
		try{
			sockInfo.getSocketOOS().flush();
			sockInfo.getSocketOOS().writeObject(fileInfo);
			FileInputStream fis=new FileInputStream(fileInfo.getFile());
			bis=new BufferedInputStream(fis);
			StatusPanels panels=UADNDListener.displayFTStatusMsg((StyledDocument)doc, fileInfo.getFile().getName());
			JPanel status=panels.getStatus();
			Component[] statusComps=status.getComponents();
			JTextField statustf=(JTextField)statusComps[0];
			Object decissionObj=sockInfo.getSocketOIS().readObject();
			if(decissionObj instanceof FTDecision && ((FTDecision)decissionObj).isFtAccepted() ){
				statustf.setText(IPMessenger.resources.getString("progressFT"));
				
				JPanel progress=panels.getProgress();
				JProgressBar jpb=new JProgressBar();
				Long filesize=fileInfo.getFile().length();	
				
				Long maxRough=filesize/3000;
				int max=0;
				if(maxRough > 0 && (filesize%3000) != 0)
					max=maxRough.intValue()+1;
				else
					max=maxRough.intValue();			
				if(max == 0)
					jpb.setMaximum(1);
				else
					jpb.setMaximum(max);	
				
				progress.add(jpb,BorderLayout.CENTER);
				int count=1;			
				while(true){
					byte[] fileBytes=new byte[3000];
					int numBytesRead=bis.read(fileBytes);				
					FileMultiPart filePart=null;
					if(numBytesRead != -1 && numBytesRead < 3000){
						fileBytes=Arrays.copyOf(fileBytes,numBytesRead);
					}
					if(numBytesRead == -1){				
						filePart=new FileMultiPart(true);
					}else{
						filePart=new FileMultiPart(fileBytes);
					}
					
					sockInfo.getSocketOOS().writeObject(filePart);
					if(numBytesRead == -1)
						break;
					jpb.setValue(count);
					count=count+1;				
				}			
				statustf.setText(IPMessenger.resources.getString("completedFT"));			
			}else
				statustf.setText(IPMessenger.resources.getString("rejectedFT"));
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);			
		}catch(ClassNotFoundException cnfe){
			logger.error(cnfe.getMessage(), cnfe);
		}finally{
			try{
				bis.close();
				sockInfo.getSocketOOS().close();
				sockInfo.getSocketOIS().close();
				sockInfo.getSocket().close();
				logger.info("Client File Transfer Thread gracefully closed.");
			}catch(IOException ioe){
				logger.error(ioe.getMessage(),ioe);
			}
		}		
	}	
}
