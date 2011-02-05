package com.ymd.gui.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.ymd.gui.util.GUIUtil.CompCenterCords;


/**
 * Create a JLogoFrame with the given image and a JProgressBar.
 *  
 * @author Muralidhar Yaragalla.
 *
 */
public class JLogoFrame extends JFrame{
	
	private static final long serialVersionUID = -4682173612336772362L;
	
	private JPanel imagePanel;
	private JProgressBar progressBar;
	
	/**
	 * Constructs JLogoFrame instance.
	 * 
	 * @param url
	 */
	public JLogoFrame(URL url){
		
		imagePanel=new JImagePanel(url);
		Dimension dim=imagePanel.getSize();
		int width=dim.width;
		int height=dim.height;
		
		Container container=getContentPane();
		container.setLayout(new BorderLayout());
		add(imagePanel,BorderLayout.CENTER);
		progressBar=new JProgressBar();		
		add(progressBar,BorderLayout.SOUTH);
		height=height+10;
		
		setSize(width,(height));
		CompCenterCords cords=GUIUtil.getCompCenterCords(width,height);
		setLocation(cords.getX(), cords.getY());
		setUndecorated(true);
		setVisible(true);
		toFront();
	}

	public JPanel getImagePanel() {
		return imagePanel;
	}

	public void setImagePanel(JPanel imagePanel) {
		this.imagePanel = imagePanel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

}
