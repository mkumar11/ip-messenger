package com.ymd.gui.util;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GUIUtil {
	
	public static JFrame displayMessage(int x,int y,String msg){
		JFrame frame=new JFrame();
		JDesktopPane dp=new JDesktopPane();
		dp.setLayout(new BorderLayout());
		
		JTextField jft=new JTextField(msg);
		jft.setEditable(false);
		dp.add(jft,BorderLayout.CENTER);
		
		frame.setContentPane(dp);
		frame.setUndecorated(true);
		frame.setLocation((x+50), (y+100));
		frame.setVisible(true);
		frame.pack();
		return frame;
	}

}
