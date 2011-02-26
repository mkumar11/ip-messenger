/**
 * 
 */
package com.ymd.util;


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
}
