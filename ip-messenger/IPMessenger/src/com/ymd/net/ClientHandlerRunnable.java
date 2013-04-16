/**
 * 
 */
package com.ymd.net;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class ClientHandlerRunnable implements Runnable {
	
	private ClientHandler clientHandler;
	private String context;
	
	public ClientHandlerRunnable(ClientHandler clientHandler,String context){
		this.clientHandler=clientHandler;
		this.context=context;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		clientHandler.connect(context);
	}

}
