package com.ymd.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Simple Network utilities.
 * 
 * @author Muralidhar Yaragalla.
 *
 */
public class NetUtil {

	/**
	 * Checks whether given port is available or not.
	 * @param port - port number.
	 * @return boolean.
	 */
	public static boolean isPortAvailable(int port) { 		   
		try {		   
			ServerSocket srv = new ServerSocket(port);			  
			srv.close();  
			srv = null;  
			return true;  
		   
		}catch (IOException e) {  
			return false;  
		}  
	} 
	
	
	/**
	 * This returns true if the given IP is same as the localhost.
	 * @param ip
	 * @return boolean.
	 */
	public static boolean localHost(String ip)throws UnknownHostException{
		InetAddress localHost=InetAddress.getLocalHost();
		String localHostName=localHost.getHostName();
		InetAddress[] localAddresses=InetAddress.getAllByName(localHostName);
		for(InetAddress add:localAddresses){
			if(ip.equals(add.getHostAddress())){
				return true;
			}
		}
		return false;
	}
}
