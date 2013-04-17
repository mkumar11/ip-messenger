/**
 * 
 */
package com.ymd.net.ft;

import java.io.Serializable;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class FTDecision implements Serializable {

	private static final long serialVersionUID = 2823320702990202253L;
	
	private boolean ftAccepted;
	
	public FTDecision(boolean ftAccepted){
		this.ftAccepted=ftAccepted;
	}

	/**
	 * @return the ftAccepted
	 */
	public boolean isFtAccepted() {
		return ftAccepted;
	}

}
