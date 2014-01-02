package com.slamtechnology.teeyai.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Toaster {

	static int currentHeight=0;
	static int screenHeight;
	static int screenWidth;
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int)screenSize.getWidth();
		screenHeight = (int)screenSize.getHeight();
	}
	public static void pop(String message){
		JFrame f = new JFrame();
		f.add(new JLabel(message));
		f.setAlwaysOnTop( true );
	    f.pack();
	    f.setVisible( true );
	    final int thisHeight = f.getHeight();
	    f.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) { currentHeight = currentHeight - thisHeight;}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	    f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    
	    currentHeight = currentHeight + thisHeight;
	    f.setLocation(screenWidth-f.getWidth(), screenHeight-currentHeight);
	}

}
