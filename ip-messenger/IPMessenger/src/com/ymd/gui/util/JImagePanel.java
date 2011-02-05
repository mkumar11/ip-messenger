package com.ymd.gui.util;

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Creates a Panel with the given Image.
 * 
 * @author Muralidhar Yaragalla.
 *
 */
public class JImagePanel extends JPanel{

	private static final long serialVersionUID = -3592125340477708263L;
	
	private Image image;
	
	/**
	 * Constructs JImagePanel instance.
	 * 
	 * @param imageURL - URL of the Image.
	 */
	public JImagePanel(URL imageURL){
		ImageIcon imageIcon=new ImageIcon(imageURL);
		int imageHeight=imageIcon.getIconHeight();
		int imagewidth=imageIcon.getIconWidth();
		setSize(imagewidth, imageHeight);		
		image=imageIcon.getImage();
	}
	
	 @Override
	    public void paintComponent(Graphics g) {
	        g.drawImage(image, 0, 0, null); 

	    }
}
