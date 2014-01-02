package com.slamtechnology.teeyai.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;

public class IndicatorChart extends JInternalFrame implements FlushableChart {

	ChartCreator chart;
	IndicatorInstanceName indicatorInstanceName;
	int width=600;
	int height=250;
	JLabel label;
	public IndicatorChart(IndicatorInstanceName indicatorInstanceName,ChartCreator chart) {
		super();
		setClosable(true);
		this.indicatorInstanceName = indicatorInstanceName;
		setSize(new Dimension(width+20,height+50));
		setMinimumSize(new Dimension(width+20,height+50));
		this.chart=chart;
		flushGraph();
	}
	
	public void flushGraph(){
		if(label==null){
			label = new JLabel();
			label.setName("chartLabel");
			add(label);
		}
		if(chart!=null){
			BufferedImage bimage = chart.getImage(width, height);
			if(bimage!=null){
				label.setIcon(new ImageIcon(bimage));
				bimage.getGraphics().dispose();
			}
		}

	}

	public void clearValues() {
		chart.clearValues();
	}
}
