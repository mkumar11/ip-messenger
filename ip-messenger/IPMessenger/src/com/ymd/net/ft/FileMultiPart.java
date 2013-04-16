package com.ymd.net.ft;

import java.io.Serializable;

public class FileMultiPart implements Serializable {

	private static final long serialVersionUID = 3606485004924878184L;
	
	private byte[] filePart;
	private boolean isLastPart;
	
	public FileMultiPart(boolean isLastPart){		
		this.isLastPart=isLastPart;
	}
	
	public FileMultiPart(byte[] filePart){
		this.filePart=filePart;
	}
	
	
	public byte[] getFilePart() {
		return filePart;
	}
	public boolean isLastPart() {
		return isLastPart;
	}
	
	

}
