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

/**
 * This is File Transfer Decision Listener Class.
 * 
 * @author yaragalla Muralidhar.
 * Modified today.
 */
public class FTDListener implements ActionListener{
	
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
			status.setText("Transfer Progress...");
			JProgressBar jpb=new JProgressBar() ;			
			decissionPanel.setLayout(new BorderLayout());
			decissionPanel.add(jpb, BorderLayout.CENTER);
			
		}else if(actionCmd.equalsIgnoreCase("reject")){
			status.setText("Transfer Rejected By Me...");			
		}
		try{
			assocFsOs.write(actionCmd.getBytes());
			assocFsOs.write(-2);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}	
}
