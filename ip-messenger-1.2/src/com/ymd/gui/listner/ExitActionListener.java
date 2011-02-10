package com.ymd.gui.listner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.ymd.net.Packets;

/**
 * This is the Listener for application Exit action.
 * 
 * @author Muralidhar Yaragalla.
 *
 */
public class ExitActionListener implements ActionListener{
	
	private MulticastSocket multicastSoc;
	private InetAddress group;
	
	/**
	 * Constructs ExitActionListener instance.
	 * 
	 * @param multicastSoc - Multicast Socket.
	 * @param group - Broadcasting IP.
	 */
	public ExitActionListener(MulticastSocket multicastSoc,InetAddress group){
		this.multicastSoc=multicastSoc;
		this.group=group;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			Packets.fireGoodbyePacket(multicastSoc, group);
			multicastSoc.leaveGroup(group);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		System.exit(0) ;
     }	

}
