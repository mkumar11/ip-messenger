/**
 * 
 */
package com.ymd.gui.listner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.ymd.log.IPMLogger;
import com.ymd.main.IPMessenger;

/**
 * This is File Transfer Decision Listener Class.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class FTDListener implements ActionListener{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private JPanel statusPanel;
	private JPanel decissionPanel;
	private OutputStream assocFsOs;
	
	/**
	 * Constructs FTDListener instance.
	 * 
	 * @param statusPanel
	 * @param decissionPanel
	 * @param assocFsOs
	 */
	public FTDListener(JPanel statusPanel,JPanel decissionPanel,OutputStream assocFsOs){
		this.statusPanel=statusPanel;
		this.decissionPanel=decissionPanel;
		this.assocFsOs=assocFsOs;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCmd=event.getActionCommand();
		Component[] statusComponents=statusPanel.getComponents();
		JTextField status=(JTextField)statusComponents[0];
		Component[] decissionComponents=decissionPanel.getComponents();
		for(Component comp:decissionComponents){
			decissionPanel.remove(comp);
		}
		
		if(actionCmd.equalsIgnoreCase("accept")){			
			status.setText(IPMessenger.resources.getString("progressFT"));
			JProgressBar jpb=new JProgressBar() ;			
			decissionPanel.setLayout(new BorderLayout());
			decissionPanel.add(jpb, BorderLayout.CENTER);
			
		}else if(actionCmd.equalsIgnoreCase("reject")){
			status.setText(IPMessenger.resources.getString("rejectedByMeFT"));			
		}
		try{
			assocFsOs.write(actionCmd.getBytes());
			assocFsOs.write(-2);
		}catch(IOException ioe){
			logger.error(ioe.getMessage(), ioe);
		}
	}	
}
