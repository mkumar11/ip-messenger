/**
 * 
 */
package com.ymd.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class SocketInfo {

	private Socket socket;
	private ObjectOutputStream socketOOS;
	private ObjectInputStream socketOIS;
	private String remoteIP;
	private String[] broadcastRemoteIPs;
	
	public SocketInfo(Socket sock,ObjectOutputStream oos,ObjectInputStream ois,String remoteIP){
		socket=sock;
		socketOOS=oos;
		socketOIS=ois;
		this.remoteIP=remoteIP;
	}
	
	public SocketInfo(Socket sock,ObjectOutputStream oos,ObjectInputStream ois,String[] broadcastRemoteIPs){
		socket=sock;
		socketOOS=oos;
		socketOIS=ois;
		this.broadcastRemoteIPs=broadcastRemoteIPs;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @return the socketOOS
	 */
	public ObjectOutputStream getSocketOOS() {
		return socketOOS;
	}

	/**
	 * @return the socketOIS
	 */
	public ObjectInputStream getSocketOIS() {
		return socketOIS;
	}

	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}

	/**
	 * @return the broadcastRemoteIPs
	 */
	public String[] getBroadcastRemoteIPs() {
		return broadcastRemoteIPs;
	}
	
	
}
