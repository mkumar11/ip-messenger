/**
 * 
 */
package com.ymd.net;

import java.io.Serializable;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class Event implements Serializable {

	private static final long serialVersionUID = -8646180296067859463L;
	
	private String event;
	
	/**
	 * Constructor of Event
	 * @param event
	 */
	public Event(String event){
		this.event=event;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}
	
	

}
