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
	 * This sends the initial packet to say that this host
	 * come live for chatting.
	 * @param multicastSoc
	 * @param group
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void sendIntialPacket(MulticastSocket multicastSoc,InetAddress group)
										throws UnknownHostException,IOException{		
		InetAddress localHost=InetAddress.getLocalHost();
		String local=localHost.getHostAddress();
		String finalLocal=Util.addPadding(local,20); 
		String name=localHost.getHostName();
		String finalName=Util.addPadding(name,20);
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="hello"+"&"+finalLocal+"&"+finalName+"&"+finalUser;
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
	public static void sendHandShakePacket(MulticastSocket multicastSoc,InetAddress group)
					throws UnknownHostException,IOException{		
		InetAddress localHost=InetAddress.getLocalHost();
		String local=localHost.getHostAddress();
		String finalLocal=Util.addPadding(local,20); 
		String name=localHost.getHostName();
		String finalName=Util.addPadding(name,20);
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="hands"+"&"+finalLocal+"&"+finalName+"&"+finalUser;
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
	public static void sendGoodbyePacket(MulticastSocket multicastSoc,InetAddress group)
						throws UnknownHostException,IOException{		
		InetAddress localHost=InetAddress.getLocalHost();
		String local=localHost.getHostAddress();
		String finalLocal=Util.addPadding(local,20); 
		String name=localHost.getHostName();
		String finalName=Util.addPadding(name,20);
		String user=System.getProperty("user.name");
		String finalUser=Util.addPadding(user,20);
		String msg="goodb"+"&"+finalLocal+"&"+finalName+"&"+finalUser;
		DatagramPacket intPacket = new DatagramPacket(msg.getBytes(), msg.length(),group, 1988);
		multicastSoc.send(intPacket);
	}

}
