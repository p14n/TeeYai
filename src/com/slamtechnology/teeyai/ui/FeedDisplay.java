package com.slamtechnology.teeyai.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.FeedMonitor;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.Price;
import static com.slamtechnology.teeyai.ui.Util.*;

public class FeedDisplay extends JInternalFrame {

	
	protected ChartCreator getChart() {
		return chart;
	}


	public boolean isIncludeGraph() {
		return includeGraph;
	}
	int interval = 0;
	int intervalCount=0;
	int height=200;
	int width=600;
	JLabel currentPrice;
	FeedMonitorName feedMonitorName;
	FeedMonitor feedMonitor;
	public FeedMonitorName getFeedMonitorName() {
		return feedMonitorName;
	}
	ChartCreator chart;
	boolean includeGraph=true;
	Indicator[] indicators;
	TeeYaiDisplay teeYaiDisplay;
	JLabel chartLabel;
	
	public FeedDisplay(
			final TeeYaiDisplay teeYaiDisplay,
			FeedMonitor feedMonitor,
			Indicator[] availableIndicators,
			boolean includeGraph
			){
		this.teeYaiDisplay = teeYaiDisplay;
		this.feedMonitor = feedMonitor;
		this.includeGraph = includeGraph;
		this.feedMonitorName = feedMonitor.getName();
		setTitle(feedMonitorName.getName());
		indicators = availableIndicators;
		setBackground(Color.WHITE);
		
		setLayout(new GridBagLayout());

		chart = feedMonitor.getChart();
		Dimension d = null;
		if(includeGraph) {
			d = new Dimension(width+20,height+100);
		} else {
			d = new Dimension(400,100);
		}
		setMinimumSize(d);
		setSize(d);
		setClosable(true);

		if(includeGraph)flushGraph();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		currentPrice = new JLabel("0");
		currentPrice.setName("currentPrice");
		//currentPrice.setMinimumSize(new Dimension(30,30));
		panel.add(currentPrice);
		
		final JComboBox indicatorBox = new JComboBox(indicators);
		panel.add(indicatorBox);
			
		JButton button = new JButton("Open");
		button.setName("indicatorOpen");
		//button.setMinimumSize(new Dimension(30,30));
		panel.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				teeYaiDisplay.addIndicator(feedMonitorName,(Indicator) indicatorBox.getSelectedItem());
			}
		});
		
		addInternalFrameListener(new InternalFrameAdapter() {

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				
				SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								teeYaiDisplay.removeFeed(feedMonitorName);
								teeYaiDisplay.remove(FeedDisplay.this);
							}
						});
				super.internalFrameClosing(e);
			}
		
		});
		add(panel,GBxy(1, 2));
		
	}
	

	public void flushGraph(){
		if(isIncludeGraph()){
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					if(chartLabel==null){
						chartLabel = new JLabel();
						chartLabel.setName("chartLabel");
						add(chartLabel,GBxy(1, 1));
					}
					if(chart!=null){
						BufferedImage bimage = chart.getImage(width, height);
						if(bimage!=null){
							chartLabel.setIcon(new ImageIcon(bimage));
							bimage.getGraphics().dispose();
						}
					}
				}
				
			});
		}
	}
	public void updatePrice(boolean backTesting){
		
		if(!backTesting){
			Price p = feedMonitor.getLatestPrice();
			currentPrice.setText(String.valueOf(p==null?"0":p.getPrice()));
			flushGraph();
		}
		
	}
}
