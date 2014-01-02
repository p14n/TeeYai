package com.slamtechnology.teeyai.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import com.slamtechnology.teeyai.FeedMonitor;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.TeeYai;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.TradeStrategy;


public class TeeYaiDesktopDisplay extends JFrame implements TeeYaiDisplay,FlushableChart {

	private TeeYai ty;
	Indicator[] availableIndicators;
	FeedComponent[] availableFeeds;
	private HashMap<FeedMonitorName,FeedDisplay> mainCharts;
	private List<FlushableChart> otherCharts;
	private HashMap<FeedMonitorName,ArrayList<IndicatorDisplay>> displays;
	TeeYaiMenu myDisplay;
	Broker[] brokers; 
	public TeeYaiDesktopDisplay(TeeYai ty,FeedComponent[] feeds,Broker[] brokers,Indicator[] availableIndicators,Map<Integer, Map<Integer, Set<Integer>>> backtestDatesToInclude) {
	
		super();
		ty.attach(this);
		this.ty = ty;
		this.availableIndicators = availableIndicators;
		this.availableFeeds=feeds;
		this.brokers = brokers;
		
		setContentPane(new JDesktopPane());
		getContentPane().
			addContainerListener(new Usher());
		
		setExtendedState(MAXIMIZED_BOTH);
		 
		mainCharts = new HashMap<FeedMonitorName, FeedDisplay>();
		otherCharts = new ArrayList<FlushableChart>();
		displays = new HashMap<FeedMonitorName,ArrayList<IndicatorDisplay>>();
		boolean feedsOpen = ty.getMonitors()!=null&&ty.getMonitors().size()>0;
		myDisplay = new TeeYaiMenu(this,feeds,ty.getTradeManager(),feedsOpen,ty.isBackTesting(), backtestDatesToInclude);
		add(myDisplay);
		
		HashMap<FeedMonitorName,FeedMonitor> monitors = ty.getMonitors();

		if(monitors!=null){
			Set<FeedMonitorName> feedMonitorNames = monitors.keySet();
			for(FeedMonitorName feedMonitorName:feedMonitorNames){
				FeedMonitor fm = monitors.get(feedMonitorName);
				addFeedChart(fm,feedMonitorName, fm.getFeed(), fm.getInterval(),null);
			}
		}
		ArrayList<Indicator> indicators = ty.getIndicators();
		if(indicators!=null){
			for(Indicator i : indicators){
				addIndicatorDisplay(i.getFeedName(), i);
			}
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	public void exit(){
		ty.detach();
		this.dispose();
	}
	public void destroy(){
		ty.destroy();
		this.dispose();
	}
	
	public void addIndicator(FeedMonitorName feedMonitorName,
			Indicator indicator) {
		indicator = indicator.getNewInstance();
		if(ty.addIndicator(feedMonitorName, indicator)){
			long now = System.currentTimeMillis();
			List<Price> history = ty.getPriceHistory(feedMonitorName.getFeedName(), now, (now-3600000));
			indicator.update(history);
			addIndicatorDisplay(feedMonitorName, indicator);
		}
	}
	private void addIndicatorDisplay(FeedMonitorName feedmonitorname,Indicator indicator){
		
		System.out.println("addIndicator "+feedmonitorname);

		IndicatorDisplay id;
		if(indicator.hasOwnChart()){
			IndicatorChart mdc = new IndicatorChart(indicator.getInstanceName(),indicator.getChart());
			//id = new IndicatorDisplay(mdc,indicator.getInstanceName());
			add(mdc);
			otherCharts.add(mdc);
			mdc.setVisible(true);
		}

		id = new StandardIndicatorDisplay(indicator.getInstanceName().getName(),this);
		
		id.setIndicator(indicator);
		
		if(!displays.containsKey(feedmonitorname)){
			ArrayList<IndicatorDisplay> ids = new ArrayList<IndicatorDisplay>();
			displays.put(feedmonitorname, ids);
		}
		displays.get(feedmonitorname).add(id);
		
		//ty.getFeedMonitorName(indicator.getFeedName(), indicator.getInterval());
		/*FeedDisplay mainChart = mainCharts.get(feedmonitorname);
		if(mainChart.isIncludeGraph()&&!indicator.hasOwnChart()){
			indicator.setChart(mainChart.getChart());
		} else {
			id.setMainChart(mainCharts.get(mainChart.getChartName()));
		}*/
		id.construct();
		add(id);
		id.setVisible(true);
	}
	public void removeIndicator(Indicator indicator,IndicatorDisplay id){
		
		remove(id);
		ty.removeIndicator(indicator);
		
	}
	public void closeIndicatorDisplay(IndicatorDisplay id){
		
		remove(id);
		
	}
	
	public void addFeed(FeedComponent feed,int interval,FeedMonitorName feedchart){
		
		//create price feed graph
		myDisplay.setBacktestingEnabled(false);
		FeedMonitorName feedMonitorName = new FeedMonitorName(feed, interval);
		if(!ty.hasFeed(feedMonitorName)){
			
			FeedMonitor fm = ty.addFeed(feed, interval,feedchart);
			addFeedChart(fm,feedMonitorName, feed, interval,feedchart);
			
		}
	}
	private void addFeedChart(FeedMonitor fm,FeedMonitorName feedMonitorName,FeedComponent feed,int interval,FeedMonitorName feedchart){
		
		/*boolean newChart = true;
		if(feedchart!=null&&mainCharts.containsKey(feedchart)){
			newChart = false;
		}*/
		//create price feed graph
		FeedDisplay f = new FeedDisplay(this,fm,availableIndicators,fm.hasOwnChart());
		/*if(!newChart){
			f.setChartName(feedchart);
		}*/
		
		mainCharts.put(feedMonitorName, f);
		
		updateChartList();
		
		add(f);
		f.setVisible(true);

	}
	/*public void removeAndAddChartsToFreeMemory(){
		Iterator<String> it = mainCharts.keySet().iterator();
		HashMap<String,FeedMonitor> monitors = ty.getMonitors();
		while(it.hasNext()){
			FeedDisplay f = mainCharts.get(it.next());
			if(f!=null&&f.isIncludeGraph()){
				String feedMonitorName = f.getFeedMonitorName();
				FeedMonitor fm = monitors.get(feedMonitorName); 
				FeedDisplay f2 = new FeedDisplay(
							fm,
							feedMonitorName,
							fm.getFeed().getName(),
							indicators,
							fm.getInterval(),
							true);
				mainCharts.remove(feedMonitorName);
				removeWindow(f);
				f.dispose();
				mainCharts.put(feedMonitorName, f2);
				addWindow(f2);
			}
		}
		for(int i=0;i<otherCharts.size();i++){
			FlushableChart fc = otherCharts.get(i);
			if(fc instanceof IndicatorChart){
				IndicatorChart md1 = (IndicatorChart)fc;
				IndicatorChart md2 = new IndicatorChart(md1.getIndicatorInstanceName(),md1.getChart());
				otherCharts.remove(i--);
				removeWindow(md1);
				md1.dispose();
				otherCharts.add(md2);
				addWindow(md2);
			}
		}
	}*/
	private void updateChartList(){
		Set<FeedMonitorName> keys = mainCharts.keySet();
		ArrayList<FeedMonitorName> openCharts = new ArrayList<FeedMonitorName>();
		for(FeedMonitorName key : keys){
			if(mainCharts.get(key).isIncludeGraph()){
				openCharts.add(key);
			}
		}
		myDisplay.constructChartCombo(openCharts);
	}
	public void addStrategy(TradeStrategy ts){
		ty.addStrategy(ts);
	}
	public void setBroker(Broker b){
		ty.setBroker(b);
	}
	public void deleteStrategy(TradeStrategy ts){
		ty.deleteStrategy(ts);
	}
	public void setBackTesting(boolean enabled){
		ty.setBackTesting(enabled);
	}
	public void removeFeed(FeedMonitorName feedMonitorName){
		
		//FeedChart f = mainCharts.get(feedMonitorName);
		mainCharts.remove(feedMonitorName);
		if(mainCharts.size()<1){
			myDisplay.setBacktestingEnabled(true);
		}
		//f.dispose();
		
		ArrayList<IndicatorDisplay> indicatorDisplays = displays.get(feedMonitorName);
		//System.out.println("removeFeed "+feedMonitorName);
		if(indicatorDisplays!=null){
			//System.out.println("removeFeed indicatorDisplays not null");
			for(IndicatorDisplay id:indicatorDisplays){
				//System.out.println("removeFeed s "+indicatorDisplays.size());
				remove(id);
				//id.dispose();
				//id = null;
			}
			displays.remove(feedMonitorName);
		}
		ty.removeFeedMonitor(feedMonitorName);
		updateChartList();

	}
	public void updatePrice(String feedName,Price price,boolean backTesting){
		
		//System.out.println("Display is being updated "+price);
		Set<FeedMonitorName> keys = mainCharts.keySet();
		//System.out.println("Keys "+(keys==null?-1:keys.size()));
		for(FeedMonitorName key : keys){
			//System.out.println("Looking for chart "+key);
			FeedDisplay fc = mainCharts.get(key);
			//System.out.println("Got chart "+fc.getName());
			if(fc.getFeedMonitorName().getFeedName().equals(feedName)){
				//System.out.println("Chart "+fc.getName()+" matches");
				fc.updatePrice(backTesting);
				//System.out.println("Chart "+fc.getName()+" matches");
				ArrayList<IndicatorDisplay> feedDisplays = displays.get(fc.getFeedMonitorName());
				if(feedDisplays!=null){
					for(int viewerCount=0;viewerCount<feedDisplays.size();viewerCount++){
						
						IndicatorDisplay viewer = feedDisplays.get(viewerCount);
						viewer.updateValues(backTesting);
						
					}
				}
			}
		}
		if(!backTesting){
			myDisplay.updateText();
			for(FlushableChart chart : otherCharts){
				chart.flushGraph();
			}
		}
	}
	public void flushGraph(){
		
		Set<FeedMonitorName> keys = mainCharts.keySet();
		for(FeedMonitorName key : keys){
			FeedDisplay fc = mainCharts.get(key);
			System.out.println("TEEYAIDISPLAY flushing "+fc.getName());
			fc.flushGraph();
		}
		for(FlushableChart chart : otherCharts){
			chart.flushGraph();
		}
		myDisplay.updateText();

	}
	/*public void clearGraphs(){
		
		Set<String> keys = mainCharts.keySet();
		for(String key : keys){
			FeedDisplay fc = mainCharts.get(key);
			if(fc.isIncludeGraph()){
				fc.clearGraphValues();
			}
		}
		
	}*/
	public void updateIndicatorOption(FeedMonitorName feedName,
			IndicatorInstanceName instanceName, String optionName, double value) {
		ty.updateIndicatorOption(feedName,instanceName, optionName, value);
	}
	public void setTradeManagerEnabled(boolean enabled){
		ty.setTradeManagerEnabled(enabled);
	}
	public void openTradeManagerDisplay(){
		TradeManagerDisplay tmd = new TradeManagerDisplay(this,ty,brokers,"Trade Manager",ty.getTradeManager(),ty.getIndicators());
		this.add(tmd);
		tmd.setVisible(true);
	}
	public void backTest(Date start,Date end){
		resetIndicators();
		clearValues();
		ty.backTest(start, end);
	}
	public boolean isReadyToTrade(){
		return ty.isReadyToTrade();
	}
	public void resetIndicators() {
		ty.reset();
	}
	public void openFeedsForEnabledStrategies() {
		
		FeedComponent first = null;
		int firstInterval = 0;
		for(TradeStrategy t:ty.getTradeManager().getTradeStrategies()){
			
			if(t!=null&&t.isEnabled()){
				for(String decision:t.getDecisions()){
					StringTokenizer st = new StringTokenizer(decision,":");
					String feedName = st.nextToken();
					int interval = Integer.parseInt(st.nextToken());
					String indicatorName = st.nextToken();
					for(FeedComponent fc:availableFeeds){
						if(fc.getName().equals(feedName)){
							FeedMonitorName chart = null;
							if(first==null){
								first = fc;
								firstInterval = interval;
								chart = new FeedMonitorName("New", 0);
							} else {
								chart = new FeedMonitorName(first, firstInterval);
							}
							addFeed(fc, interval, chart);
							for(Indicator i: availableIndicators){
								if(i.getIndicatorName().equals(indicatorName)){
									addIndicator(new FeedMonitorName(fc, interval), i);
								}
							}
						}
						
					}
				}
			}
			
		}
		
	}
	public void clearValues() {
		for(FlushableChart c:otherCharts){
			c.clearValues();
		}
	}

}
