/**
 * 
 */
package com.ymd.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import com.ymd.util.Util;

/**
 * This is the class for all required packets on the network 
 * for this chat application.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class Packets {
	
	
	/**
	 * This sends the hello packet to say that this host
	 * come live for chatt.
	 * @param multicastSoc
	 * @param group
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void fireHelloPacket(MulticastSocket multicastSoc,InetAddress group)
										throws UnknownHostException,IOException{		
		
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="hello"+"&"+finalUser;
		DatagramPacket intPacket = new DatagramPacket(msg.getBytes(), msg.length(),group, 1988);
		multicastSoc.send(intPacket);
	}
	
	/**
	 * This sends a handshake packet to say that this host is 
	 * online and ready for chat.
	 * @param multicastSoc
	 * @param group
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void fireHandShakePacket(MulticastSocket multicastSoc,InetAddress group)
					throws UnknownHostException,IOException{		
		
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="hands"+"&"+finalUser;
		DatagramPacket intPacket = new DatagramPacket(msg.getBytes(), msg.length(),group, 1988);
		multicastSoc.send(intPacket);
	}
	
	/**
	 * This sends Good bye packet.
	 * @param multicastSoc
	 * @param group
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void fireGoodbyePacket(MulticastSocket multicastSoc,InetAddress group)
						throws UnknownHostException,IOException{		
		
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="goodb"+"&"+finalUser;
		DatagramPacket intPacket = new DatagramPacket(msg.getBytes(), msg.length(),group, 1988);
		multicastSoc.send(intPacket);
	}

}
