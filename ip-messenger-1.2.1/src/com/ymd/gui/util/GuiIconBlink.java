/**
 * 
 */
package com.ymd.gui.util;

import java.awt.Image;
import java.util.List;

import javax.swing.JFrame;

import com.ymd.log.IPMLogger;

/**
 * This thread blinks the given JFrame icon when the frame 
 * is not in focus.
 * 
 * @author yaragalla Muralidhar.
 * 
 */
public class GuiIconBlink implements Runnable{
	
	private IPMLogger logger=IPMLogger.getLogger();
	
	private JFrame frame;
	private List<Image> blinkImages;
	private Image defaultImg;
	
	public GuiIconBlink(JFrame frame,List<Image> blinkImages,Image defaultImg){
		this.frame=frame;
		this.blinkImages=blinkImages;
		this.defaultImg=defaultImg;
	}

	@Override
	public void run() {
		try{
			while(true){
				for(Image blinkImage:blinkImages){					
					frame.setIconImage(blinkImage);
					Thread.sleep(500);	
					if(frame.isFocused())
						break;
				}
				if(frame.isFocused())
					break;
			}
			
		}catch(InterruptedException ie){
			logger.error(ie.getMessage(), ie);
		}finally{
			frame.setIconImage(defaultImg);
		}
	}		
}


