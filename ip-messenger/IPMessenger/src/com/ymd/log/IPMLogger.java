package com.ymd.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This is the Logger for IPMessenger.
 * @author yaragamu
 * modified today.
 */
public class IPMLogger extends Logger{	
	
	private static IPMLogger ipmLogger;
	
	/**
	 * Constructs IPMLogger Instance.
	 * @param logger - Logger.
	 */
	private IPMLogger(String name){
		super(name,null );
	}
	
	/**
	 * This returns IPMLogger configured to log messages in a file.
	 * @return - IPMLogger
	 */
	public static IPMLogger getLogger(){
		if(ipmLogger == null){
			IPMLogger logger=new IPMLogger("log");					
			File file=new File("");					
			FileHandler fh=null;
			try{
				fh=new FileHandler(file.getAbsolutePath()+"/IPMessenger%g.log");
				fh.setFormatter(new SimpleFormatter());
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			logger.addHandler(fh);
			ipmLogger=logger;
			return ipmLogger;
		}
		return ipmLogger;
	}	
	
	/**
	 * This logs message at SEVERE level.
	 * @param msg - message to be logged.
	 * @param thrown - Throwable.
	 */
	public void error(String msg,Throwable thrown){
		StackTraceElement[]stElement=new Throwable().getStackTrace();
		String className=stElement[1].getClassName();
		String methodName=stElement[1].getMethodName();		
		logp(Level.SEVERE,className,methodName,msg,thrown); 		
	}	
	
}
