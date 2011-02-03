package com.ymd.gui;

import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.ymd.gui.dnd.listner.UADNDListener;
import com.ymd.gui.listner.ChatGUIUAListener;

/**
 * This is the GUI class for chat window.
 * 
 * @author yaragalla Muralidhar
 *
 */
public class ChatGui extends JFrame {
	
	private static final long serialVersionUID = 4942132120249803370L;
	
	private JTextPane ma;
	private JTextArea ua;	
	
	private final OutputStream out;
	
	/**
	 * Constructor which configures the main properties 
	 * and creates the required GUI.
	 * @param title
	 * @param out
	 */
	public ChatGui(String ip,final OutputStream out){
		super(ip+":-IPMessenger");
		this.out=out;
		JDesktopPane dp=new JDesktopPane();
		dp.setLayout(null);
		this.setContentPane(dp);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try{
					out.write(-1);
				}catch(IOException ioe){
					System.out.println(ioe);
				}
			}
		});
		
		ma=new JTextPane();
		ma.setEditable(false);
		JScrollPane jspma=new JScrollPane(ma);		
		jspma.setBounds(0, 0, 250, 300);
		dp.add(jspma);
		
		ua=new JTextArea();
		ua.setToolTipText("Enter your chat message here...");
		ua.setDragEnabled(true);
		ua.setLineWrap(true);
		ua.setWrapStyleWord(true);
		ua.addKeyListener(new ChatGUIUAListener(this));
		new DropTarget(ua,new UADNDListener(ip));
		JScrollPane jspua=new JScrollPane(ua);		
		jspua.setBounds(0, 300, 250, 100);		
		dp.add(jspua);		
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setSize(260, 450);
		this.setVisible(true);
	}
	
	
	

	public JTextPane getMa() {
		return ma;
	}

	public JTextArea getUa() {
		return ua;
	}	

	public OutputStream getOut() {
		return out;
	}
		
}
