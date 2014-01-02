package com.slamtechnology.teeyai.ui.test;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class TestUtil {
	
	public static JDesktopPane desktopFor(JInternalFrame f){
		JDesktopPane desktop = new JDesktopPane();
		desktop.add(f);
		f.show();
		return desktop;
	}

}
