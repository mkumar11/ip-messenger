/**
 * 
 */
package com.ymd.util;

import java.util.UUID;

/**
 * This is Globally Unique Identifier Generator.
 * 
 * @author yaragalla Muralidhar.
 * 
 * modified Today.
 */
public class GUID {
	
	/**
	 * This method generates a strong 128 bit unique global Id.
	 *  
	 * @return String - unique id.
	 */
	public static String generateId(){
		String guid=null;
		UUID uuid=UUID.randomUUID();
		guid=uuid.toString();
		return guid;
	}
	
	/**
	 * This method generates a strong 128 bit unique global 
	 * Id with a given prefix appended.
	 * 
	 * @param prefix-prefix that should be appended to the ID.
	 * @return String - unique id.
	 */
	public static String generateId(String prefix){
		String guid=null;
		UUID uuid=UUID.randomUUID();
		guid=uuid.toString();
		return guid;
	}
}
