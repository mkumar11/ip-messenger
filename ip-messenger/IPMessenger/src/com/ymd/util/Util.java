/**
 * 
 */
package com.ymd.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is a utility class for some common functionalities used 
 * across the application.
 * 
 * @author yaragalla Muralidhar.
 *
 */
public class Util {
	
	/**
	 * This method adds padding to the given string to populate 
	 * to a particular length.
	 * @param input
	 * @param reqLength
	 * @return padded string.
	 */
	public static String addPadding(String input,int reqLength){
		int length=input.length();
		StringBuffer finalstr=new StringBuffer();
		if(length<reqLength){
			finalstr.append(input);
			finalstr.append(";");
			for(int i=length;i<(reqLength-1);i++){
				finalstr.append("0");
			}			
		}
		return finalstr.toString();
	}
	
	/**
	 * Removes padding from the given String.
	 * @param token
	 * @return unpadded string.
	 */
	public static String removePadding(String token){
		String tok[]=token.split(";");
		return tok[0];
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
