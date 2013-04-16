/**
 * 
 */
package com.ymd.net.ft;

import java.io.File;
import java.io.Serializable;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class FileInfo implements Serializable {

	private static final long serialVersionUID = -7319443615527011200L;
	protected File file;
	protected String chatId;
	
	public FileInfo(File file,String chatId){
		this.file=file;
		this.chatId=chatId;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the chatId
	 */
	public String getChatId() {
		return chatId;
	}
	
	

}
