/**
 * 
 */
package com.ymd.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ymd.main.resources.Dummy;

/**
 * @author Muralidhar Yaragalla.
 *
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
	
		private static final long serialVersionUID = -4500172171048801568L;
	
	
	    private JLabel label;

	    public CustomTreeCellRenderer() {
	        label = new JLabel();
	        label.setOpaque(true);	        
	    }

	    @Override
	    public Component getTreeCellRendererComponent(
	        JTree tree,
	        Object value,
	        boolean selected,
	        boolean expanded,
	        boolean leaf,
	        int row,
	        boolean hasFocus) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
	        Object obj=node.getUserObject();
	        String ip=null;
	        if(obj instanceof String)
	        	ip = (String)obj;	        
	        ImageIcon nodeImg=new ImageIcon(Dummy.class.getResource("node.png"));	        
        	label.setIcon(nodeImg);
	        label.setText(ip);
	        label.setToolTipText("Click On IP To Establish Connection");
	        if (selected) {
	            label.setBackground(backgroundSelectionColor);
	        } else {
	            label.setBackground(backgroundNonSelectionColor);
	        }
	        return label;
	    }

}
