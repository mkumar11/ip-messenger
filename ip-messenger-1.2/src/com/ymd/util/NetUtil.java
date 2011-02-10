package com.ymd.util;

import java.io.IOException;
import java.net.ServerSocket;

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
}
