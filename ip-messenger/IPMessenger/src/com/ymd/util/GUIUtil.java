package com.ymd.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Simple GUI utilities class.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class GUIUtil {
	
	/**
	 * creates a non decorated JFrame to display simple messages.
	 * @param x - x position on screen.
	 * @param y - y position on screen.
	 * @param msg - message that is to be displayed.
	 * @return JFrame.
	 */
	public static JFrame displayMessage(int x,int y,String msg){
		JFrame frame=new JFrame();
		JDesktopPane dp=new JDesktopPane();
		dp.setLayout(new BorderLayout());
		
		JTextArea jft=new JTextArea(msg);
		jft.setEditable(false);
		jft.setBackground(Color.WHITE);
		dp.add(jft,BorderLayout.CENTER);
		
		frame.setContentPane(dp);
		frame.setUndecorated(true);
		frame.setLocation((x+50), (y+100));
		frame.setVisible(true);
		frame.toFront();
		frame.pack();
		return frame;
	}	
	
	
	/**
	 * Given component width and height it returns cords which 
	 * can set the component at the mid of desktop.
	 *  
	 * @param width
	 * @param height
	 * @return
	 */
	public static CompCenterCords getCompCenterCords(int width,int height){
		CompCenterCords cords=new CompCenterCords();
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Dimension dim=toolkit.getScreenSize();
		int screenHeight=dim.height;
		int screenWidth=dim.width;
		int heightMidPoint=screenHeight/2;
		int widthMidPoint=screenWidth/2;
		int compX=widthMidPoint-(width/2);
		cords.setX(compX);
		int compY=heightMidPoint-(height/2);
		cords.setY(compY);
		return cords;
	}
	
	/**
	 * It holds desktop center cords.
	 * 
	 * @author Muralidhar Yaragalla.
	 *
	 */
	public static class CompCenterCords{
		private int x;
		private int y;
		
		
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}		
	}
}
