package com.slamtechnology.teeyai.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.TeeYai;
import com.slamtechnology.teeyai.prices.PriceFeed;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.ExecutionListener;
import com.slamtechnology.teeyai.trade.Position;
import com.slamtechnology.teeyai.trade.TradeConfiguration;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.TradeStrategy;
import com.slamtechnology.teeyai.trade.brokers.WorldSpreadsDemoFTSEConfig;
import com.slamtechnology.teeyai.trade.brokers.WorldSpreadsDemoWallStConfig;

import static com.slamtechnology.teeyai.ui.Util.*;
public class TradeManagerDisplay extends JInternalFrame {
	
	TradeManager tm;
	ComboBox<TradeConfiguration> configBox = null;
	ComboBox<TradeStrategy> strategies ;
	ComboBox<ExecutionMarker> executionTypes ;
	JTextField strategyName;
	ArrayList<StrategyCheckBox> strategyMarkers;
	JTextField targetText;
	JTextField stopText;
	JTextField takeText;
	JTextField amount;
	JTextField spread;
	JTextField initialStop;
	JTextField tolerance;
	JTextField maxtrades;
	JTextField minSwing;
	JTextField openingTime;
	
	ComboBox<Broker> brokerSelect;
	protected boolean strategyChanged;
	TeeYai ty;
	TeeYaiDisplay tyd;
	
	
	public TradeManagerDisplay(TeeYaiDisplay tyDisplay,TeeYai ty,Broker[] brokers,String title,final TradeManager tm,ArrayList<Indicator> indicators) {
		super(title,true,true);
		setLayout(new GridBagLayout());
		this.ty = ty;
		this.tm = tm;
		this.tyd = tyDisplay;
		Dimension d = new Dimension(900,(indicators.size()*10)+550); 
		setSize(d);
		setMinimumSize(d);
		setResizable(true);

		GBHelper pos = new GBHelper();
		add(createOptionsPanel(),pos.align(NORTHWEST));
		add(createStrategyPanel(indicators),pos.nextCol().align(SOUTHWEST));
		add(populateStrategyEnablePanel(),pos.nextCol().height(3));
		add(createBrokerPanel(brokers),pos.nextRow().align(NORTHWEST));
		add(createConfigPanel(),pos.nextCol().align(NORTHWEST));
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				Broker b=null;
				if(0!=brokerSelect.getSelectedItem().getID()){
					b = brokerSelect.getSelectedItem();
					tm.setBroker(b);
				} else {
					tm.setBroker(null);
				}
				tm.setTargetProfit(Double.parseDouble(targetText.getText()));
				tm.setStopSpread(Double.parseDouble(stopText.getText()));
				tm.setProfitTake(Double.parseDouble(takeText.getText()));
				tm.setAmount(Double.parseDouble(amount.getText()));
				tm.setSpread(Double.parseDouble(spread.getText()));
				tm.setStop(Double.parseDouble(initialStop.getText()));
				tm.setPriceTolerance(Double.parseDouble(tolerance.getText()));
				tm.setMaxTrades(Integer.parseInt(maxtrades.getText()));
				tm.setOpeningTime(openingTime.getText());
				tm.setMinSwing(Integer.parseInt(minSwing.getText()));
				saveStrategy();
			}
		});
		add(save,pos.nextRow().align(SOUTHEAST));
		System.out.println("completed trade manager display construct");

	}
	
	private JPanel createStrategyPanel(ArrayList<Indicator> indicators){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Strategies"));
		createStrategyBox();
		
		//panel.setMinimumSize(new Dimension(100,300));
		
		GBHelper pos = new GBHelper();
		
		panel.add(strategies,pos.width(3).align(WEST));
		
		
		ActionListener strategyChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				strategyChanged = true;
			}
		};
		
		panel.add(new JLabel("Name"),pos.nextRow().align(WEST));
		strategyName = new JTextField();
		strategyName.setColumns(10);
		strategyName.addActionListener(strategyChangedListener);
		panel.add(strategyName,pos.nextCol().width(2).align(EAST));
		
		
		
		
		panel.add(new JLabel("Type"),pos.nextRow().align(WEST));

		executionTypes = new ComboBox<ExecutionMarker>();
		executionTypes.addItem(new ExecutionMarker("Buy",TradeStrategy.BUY));
		executionTypes.addItem(new ExecutionMarker("Sell",TradeStrategy.SELL));
		executionTypes.addItem(new ExecutionMarker("Short",TradeStrategy.SHORT));
		executionTypes.addItem(new ExecutionMarker("Cover",TradeStrategy.COVER));
		executionTypes.addActionListener(strategyChangedListener);
		
		panel.add(executionTypes,pos.nextCol().width(2).align(EAST));

		strategyMarkers = new ArrayList<StrategyCheckBox>();
		if(indicators!=null){
			for(Indicator i : indicators){
				StrategyCheckBox c = new StrategyCheckBox(i.getInstanceName());
				strategyMarkers.add(c);
				c.addActionListener(strategyChangedListener);
				panel.add(c,pos.nextRow().width(3).align(WEST));
			}
		}

		JButton del = new JButton("Delete");
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteStrategy();
			}
		});
		panel.add(del,pos.nextRow().nextCol().nextCol().align(EAST));


		return panel;
	}
	private void buildOptionRow(JPanel panel,String text,JTextField field){
		panel.add(new JLabel(text));
		panel.add(field);
	}
	private JPanel createBrokerPanel(Broker[] brokers){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Broker"));
		brokerSelect = new ComboBox<Broker>();
		Broker empty = new Broker(){
			public long getID(){return 0;}
			public void execute() {}
			public String getName() { return "None";}
			public String toString() { return "None";}
			public void setClosePosition(ExecutionListener ex, Position p,double tolerance) {}
			public void setOpenPosition(ExecutionListener ex, Position p,double tolerance) {}
			
		};
		long brokerID = 0;
		if(tm.getBroker()!=null){
			brokerID = tm.getBroker().getID();
		}
		
		brokerSelect.addItem(empty);
		for(Broker broker : brokers){
			brokerSelect.addItem(broker);
			if(brokerID==broker.getID()){
				brokerSelect.setSelectedItem(broker);
			}
		}
		
		panel.add(brokerSelect,GBxyhw(1, 1, 1, 3));
		
		JButton buy = new JButton("Buy");
		buy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						tm.buy(null);
						tyd.flushGraph();
					}
				});
			}
		});
		JButton sell = new JButton("Sell");
		sell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						tm.sell(null);
						tyd.flushGraph();
					}
				});
			}
		});
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						tm.closeAll();
						tyd.flushGraph();
					}
				});
			}
		});
		panel.add(buy,GBxy(1, 2));
		panel.add(sell,GBxy(2, 2));
		panel.add(close,GBxy(3, 2));

		return panel;
	}
	private JPanel createConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Config"));
		createConfigSelect();
		panel.add(configBox,GBxyhw(1, 1, 1, 1));
		return panel;
	}

	private void updateOptions(){
		targetText.setText(String.valueOf(tm.getTargetProfit()));
		stopText.setText(String.valueOf(tm.getStopSpread()));
		takeText.setText(String.valueOf(tm.getProfitTake()));
		amount.setText(String.valueOf(tm.getAmount()));
		spread.setText(String.valueOf(tm.getSpread()));
		initialStop.setText(String.valueOf(tm.getStop()));
		tolerance.setText(String.valueOf(tm.getPriceTolerance()));
		maxtrades.setText(String.valueOf(tm.getMaxTrades()));
		minSwing.setText(String.valueOf(tm.getMinSwing()));
		openingTime.setText(tm.getOpeningTime()+"-"+tm.getClosingTime());
	}
	private JPanel createOptionsPanel(){
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Options"));
		targetText = new JTextField();
		buildOptionRow(panel,"Target profit", targetText );

		stopText = new JTextField();
		buildOptionRow(panel,"Trailing stop", stopText );

		takeText = new JTextField();
		buildOptionRow(panel,"Take profit", takeText );
		
		amount = new JTextField();
		buildOptionRow(panel,"Amount", amount );

		spread = new JTextField();
		buildOptionRow(panel,"Spread", spread );

		initialStop = new JTextField();
		buildOptionRow(panel,"Initial stop", initialStop );
		
		tolerance = new JTextField();
		buildOptionRow(panel,"Price tolerance", tolerance );

		maxtrades = new JTextField();
		buildOptionRow(panel,"Max trades", maxtrades );

		minSwing = new JTextField();
		buildOptionRow(panel,"Min swing", minSwing );

		openingTime = new JTextField();
		buildOptionRow(panel,"Start trading from", openingTime );

		updateOptions();
		return panel;
	}
	
	private void createConfigSelect(){
		
		
		if(configBox==null){
			configBox = new ComboBox<TradeConfiguration>();
		}
		configBox.removeAllItems();
		configBox.addItem(new TradeConfiguration(){
			@Override
			public String toString() {
				return "Default";
			}
		});
		configBox.addItem(new WorldSpreadsDemoFTSEConfig());
		configBox.addItem(new WorldSpreadsDemoWallStConfig());
		configBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				tm.setConfig(configBox.getSelectedItem());
				updateOptions();
				brokerSelect.setSelectedItem(configBox.getSelectedItem().getBroker());
			}
		});
	}
	
	private void createStrategyBox(){

		TradeStrategy dummy = new TradeStrategy("New",TradeStrategy.BUY);
		boolean newbox=false;
		if(strategies==null){
			strategies = new ComboBox<TradeStrategy>();
			newbox=true;
		} else {
			strategies.removeAllItems();
		}

		strategies.addItem(dummy);

		if(tm!=null&&tm.getTradeStrategies()!=null){
			for(TradeStrategy tstrat:tm.getTradeStrategies()){
				if(tstrat!=null){
					strategies.addItem(tstrat);
				}
			}
		}

		if(newbox){

			strategies.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					TradeManagerDisplay.this.PopulateDataFields(strategies.getSelectedItem());
					strategyChanged = false;
				}
			});
		}
		strategies.setSelectedIndex(0);
		strategyChanged = false;
	}
	
	private JPanel populateStrategyEnablePanel(){
		JPanel panel = new JPanel(new GridLayout(8,1));
		for(final TradeStrategy strategy:tm.getTradeStrategies()){
			if(strategy!=null){
				JCheckBox b = new JCheckBox(strategy.getName());
				
				b.setSelected(strategy.isEnabled());
				
				b.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						strategy.setEnabled(!strategy.isEnabled());
					}
				});
				panel.add(b);
			}
		}
		JButton addEnabled = new JButton("Open feeds for Strategies");
		addEnabled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						tyd.openFeedsForEnabledStrategies();
					}
				});
			}
		});
		panel.add(addEnabled);
		return panel;
	}
	
	private class ExecutionMarker {
		
		String name;
		int type;
		public ExecutionMarker(String name,int type){
			this.name=name;
			this.type=type;
		}
		public String getName() {
			return name;
		}
		public int getType() {
			return type;
		}
		public String toString(){
			return getName();
		}
		
	}
	
	private void saveStrategy(){
		
		if(strategyChanged){
			boolean newStrategy = false;
			TradeStrategy ts = strategies.getSelectedItem();
			if(ts==null){
				ts=new TradeStrategy();
				newStrategy = true;
			}
			ts.setName(strategyName.getText());
			ts.removeIndicators();
			for(StrategyCheckBox i : strategyMarkers){
				if(i.isSelected()){
					ts.addIndicator(i.getIndicatorInstanceName());
				}
			}
			ts.setExecutionType(executionTypes.getSelectedItem().getType());
			ty.addStrategy(ts);
			createStrategyBox();
		}
		
	}
	private void deleteStrategy(){
		
		TradeStrategy ts = strategies.getSelectedItem();
		ty.deleteStrategy(ts);
		createStrategyBox();
		
	}
	private void PopulateDataFields(TradeStrategy ts){
		if(ts!=null){
			strategyName.setText(ts.getName());
			for(StrategyCheckBox i : strategyMarkers){
				if(ts.isIndicator(i.getIndicatorInstanceName())){
					i.setSelected(true);
				} else {
					i.setSelected(false);
				}
			}
			switch(ts.getExecutionType()){
				case TradeStrategy.BUY:
					executionTypes.setSelectedIndex(0);
					break;
				case TradeStrategy.SELL:
					executionTypes.setSelectedIndex(1);
					break;
				case TradeStrategy.SHORT:
					executionTypes.setSelectedIndex(2);
					break;
				case TradeStrategy.COVER:
					executionTypes.setSelectedIndex(3);
					;
			}
		}
	}


}
