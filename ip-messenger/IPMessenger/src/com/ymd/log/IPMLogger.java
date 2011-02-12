package com.ymd.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class IPMLogger {
	
	private Logger logger;
	
	/**
	 * Constructs IPMLogger Instance.
	 * @param logger - Logger.
	 */
	private IPMLogger(Logger logger){
		this.logger=logger;
	}
	
	/**
	 * This returns IPMLogger configured to log messages in a file.
	 * @return - IPMLogger
	 */
	public static IPMLogger getLogger(){
		Logger logger=Logger.getLogger("log");
		File file=new File("");
		FileHandler fh=null;
		try{
			fh=new FileHandler(file.getAbsolutePath()+"/IPMessenger%g.log");
			fh.setFormatter(new SimpleFormatter());
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		logger.addHandler(fh);
		return new IPMLogger(logger);
	}
	
	/**
	 * This logs message at info level.
	 * @param msg - message to be logged.
	 */
	public void info(String msg){
		logger.info(msg);
	}
	
	/**
	 * This logs message at SEVERE level.
	 * @param msg - message to be logged.
	 * @param thrown - Throwable.
	 */
	public void error(String msg,Throwable thrown){
		logger.log(Level.SEVERE, msg, thrown);
	}
	
	/**
	 * This logs message at SEVERE level.
	 * @param msg - message to be logged.
	 */
	public void error(String msg){
		logger.log(Level.SEVERE, msg);
	}
}
