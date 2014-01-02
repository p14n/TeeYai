package com.slamtechnology.teeyai.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.michaelbaranov.microba.calendar.DatePicker;
import com.michaelbaranov.microba.calendar.VetoPolicy;
import com.michaelbaranov.microba.common.PolicyListener;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.PriceFeed;
import com.slamtechnology.teeyai.trade.Position;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.util.LogSetup;

import static com.slamtechnology.teeyai.ui.Util.*;

public class TeeYaiMenu extends JInternalFrame {
	
	ComboBox<FeedComponent> sb;
	ComboBox<FeedMonitorName> charts;
	JTextField text;
	JTextArea openPositions;
	JTextArea recentPositions;
	TradeManager tm;
	JCheckBox backtesting;
	DatePicker startdate;
	DatePicker enddate;
	JButton backtest;
	JLabel readyText;
	TeeYaiDisplay display;
	
	public void setBacktestingEnabled(boolean enabled) {
		this.backtesting.setEnabled(enabled);
	}
	
	public void constructChartCombo(List<FeedMonitorName> chartNames){

		System.out.println("reconstruct chart list ");
		if(charts==null){
			charts = new ComboBox<FeedMonitorName>();
		} else {
			charts.removeAllItems();
		}
		charts.addItem(new FeedMonitorName("New", 0));
		if(chartNames!=null){
			for(FeedMonitorName chart : chartNames){
				charts.addItem(chart);
			}
		}
		
	}
	
	private JPanel createFeedMenu(FeedComponent[] feeds){
		
		JPanel feedPanel = new JPanel();
		feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.X_AXIS));
		feedPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Feeds"));
		sb = new ComboBox<FeedComponent>();
		
		for(FeedComponent feed : feeds){
			sb.addItem(feed);
		}
		
		feedPanel.add(sb);
		setBackground(Color.WHITE);

		text = new JTextField("");
		text.setColumns(4);
		text.setPreferredSize(new Dimension(50,20));
		feedPanel.add(new JLabel("Interval:"));
		feedPanel.add(text);
		
		constructChartCombo(null);
		
		feedPanel.add(charts);

		JButton b = new JButton("Open");
		b.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						FeedComponent pf = sb.getSelectedItem();
						int interval=1;
						String intervalText = text.getText();
						if(intervalText!=null&&!"Interval".equals(intervalText)){
							try{
								interval = Integer.parseInt(intervalText);
							} catch (Exception e){}
						}
						display.addFeed(pf, interval, charts.getSelectedItem());
					}
				});
		
		feedPanel.add(b);
		return feedPanel;
	}
	private JPanel createTradeManagerMenu(){
		
		JPanel tradePanel = new JPanel();
		tradePanel.setLayout(new BoxLayout(tradePanel, BoxLayout.X_AXIS));
		tradePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Trade manager"));
		JButton editManager = new JButton("Edit");
		editManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.openTradeManagerDisplay();
			}
		});
		tradePanel.add(editManager);

		final JButton enableManager = new JButton(tm.isTrading()?"Disable":"Enable");
		enableManager.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				display.setTradeManagerEnabled(!tm.isTrading());
				enableManager.setText(tm.isTrading()?"Disable":"Enable");
			}
		});
		tradePanel.add(enableManager);

		
		readyText = new JLabel("Not ready");
		final JButton checkManager = new JButton("Check");
		checkManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tm.isReadyToTrade()){
					readyText.setText("Ready");
				} else {
					readyText.setText("Not Ready");
				}
			}
		});
		tradePanel.add(readyText);
		tradePanel.add(checkManager);
		
		final JButton reset = new JButton("Reset indicators");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.resetIndicators();
			}
		});
		tradePanel.add(reset);

		
		return tradePanel;
	}
	
	public JPanel createBackTestingPanel(boolean isBackTesting,boolean feedsOpen, Map<Integer, Map<Integer, Set<Integer>>> backtestDatesToInclude){ 
		
		JPanel backPanel = new JPanel();
		backPanel.setLayout(new BoxLayout(backPanel, BoxLayout.X_AXIS));
		backPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Back testing"));
		
		backtesting = new JCheckBox();
		backtesting.setSelected(isBackTesting);
		backtesting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.setBackTesting(backtesting.isSelected());
				backtest.setEnabled(backtesting.isSelected());
			}
		});
		if(feedsOpen){
			backtesting.setEnabled(false);
		}
		backPanel.add(new JLabel("Enabled"));
		backPanel.add(backtesting);
		//DatePicker picker = new DatePicker();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		VetoPolicy vp = null;
		if(backtestDatesToInclude!=null){
			vp = new DatePickerVetoPolicy(backtestDatesToInclude);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		startdate = new DatePicker(cal.getTime());
		startdate.setDateFormat(df);
		startdate.setStripTime(false);
		startdate.setKeepTime(true);
		//startdate.setColumns(12);
		backPanel.add(startdate);
		cal.set(Calendar.HOUR_OF_DAY, 17);
		enddate = new DatePicker(cal.getTime());
		enddate.setDateFormat(df);
		enddate.setStripTime(false);
		enddate.setKeepTime(true);
		
		if(vp!=null){
			startdate.setVetoPolicy(vp);
			enddate.setVetoPolicy(vp);
		}
		//enddate.setColumns(12);
		backPanel.add(enddate);

		backtest = new JButton("Backtest");
		backtest.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				display.backTest(startdate.getDate(),enddate.getDate());
				Logger log = LogSetup.backLog;
				log.info("Logging backtest session from "+startdate.getDate()+" to "+enddate.getDate());
				log.info("Closed positions");
				log.info(getPositionText(tm.getRecentPositions()));
				log.info("Open positions");
				log.info(getPositionText(tm.getOpenPositions()));
			}
		});
		backPanel.add(backtest);
		return backPanel;
	}
	public JPanel createPositionPanel(){ 
		JPanel posPanel = new JPanel();
		posPanel.setLayout(new BoxLayout(posPanel, BoxLayout.Y_AXIS));
		posPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Back testing"));
		openPositions = new JTextArea("Open positions");
		openPositions.setMinimumSize(new Dimension(520,50));
		openPositions.setColumns(45);
		openPositions.setRows(5);
		posPanel.add(new JScrollPane(openPositions, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		recentPositions = new JTextArea("Recent positions");
		recentPositions.setMinimumSize(new Dimension(520,150));
		recentPositions.setColumns(45);
		recentPositions.setRows(15);
		posPanel.add(new JScrollPane(recentPositions, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		setMinimumSize(new Dimension(520,200));
		return posPanel;
	}
	public TeeYaiMenu(final TeeYaiDisplay display,FeedComponent[] feeds,final TradeManager tm,boolean feedsOpen,boolean isBackTesting,Map<Integer, Map<Integer,Set<Integer>>> backtestDatesToInclude){//boolean tradeManagerActive){
		
		
		super("TeeYai",false,false);
		setLayout(new GridBagLayout());
		this.display=display;
		this.setSize(new Dimension(650, 600));
		this.setMinimumSize(new Dimension(650, 600));
		this.setMaximumSize(new Dimension(650, 600));
		
		this.tm=tm;

		add(createFeedMenu(feeds),GBxy(1, 1));
		add(createTradeManagerMenu(),GBxy(1, 2));
		add(createBackTestingPanel(isBackTesting, feedsOpen, backtestDatesToInclude),GBxy(1, 3));
		add(createPositionPanel(),GBxy(1, 4));

		
		
		this.setVisible(true);
		
	}

	private void setOpenPositionText(ArrayList<Position> positions){
		openPositions.setText("Open positions\n"+getPositionText(positions));
	}
	private void setRecentPositionText(ArrayList<Position> positions){
		recentPositions.setText("Recent positions\n"+getPositionText(positions));
	}
	public static String getPositionText(ArrayList<Position> positions){
		StringBuffer sb = new StringBuffer("");
		SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		double profit = 0;
		if(positions!=null){
			for(Position p : positions){
				if(p!=null){
					profit = profit+p.getProfit();
					Date closed = p.getClosed();
					Date opened = p.getOpened();
					long openFor = -1;
					if(closed!=null&&opened!=null){
						openFor=(closed.getTime()-opened.getTime())/60000;
					}
					
					//lokks like opened is stil null
					sb.append(sdf.format(opened)+" "+(p.isLong()?"Buy":"Short")+" of "+p.getFeedName()+
							" at "+p.getEntryPoint()+
							", profit "+p.getProfit()+
							" hwm "+(p.isLong()?p.getHighPoint()-p.getEntryPoint():p.getEntryPoint()-p.getLowPoint())+
							" lwm "+(p.isLong()?p.getLowPoint()-p.getEntryPoint():p.getEntryPoint()-p.getHighPoint())+
							(openFor>-1?"\nOpen for "+openFor+" mins ":" still open ")+
							"\n");
					sb.append("Strategies: Open: "+p.getOpeningStrategy());
					sb.append(". Closed: "+p.getClosingStrategy()+
							"\n");
				}
			}
		}
		return "Total profit : "+profit+"\n"+sb.toString();
	}
	public void updateText(){
		setOpenPositionText(tm.getOpenPositions());
		setRecentPositionText(tm.getRecentPositions());
	}

}
