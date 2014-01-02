package com.slamtechnology.teeyai.ui;

import java.awt.GridBagConstraints;

public class Util {
	
	public static GridBagConstraints GBxy(int x,int y){
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = x;
		gb.gridy = y;
		return gb;
	}
	public static GridBagConstraints GBxyhw(int x,int y,int height,int width){
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = x;
		gb.gridy = y;
		gb.gridheight = height;
		gb.gridwidth = width;
		return gb;
	}

}
