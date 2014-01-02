package com.slamtechnology.teeyai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.Position;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.TradeStrategy;


public class TeeYai  {
	
	private HashMap<FeedMonitorName,FeedMonitor> monitors;
	private HashMap<FeedMonitorName,ChartCreator> charts;
	private HashMap<String,ArrayList<FeedMonitor>> feedMonitors;
	private TradeManager tm;
	private TeeYaiListener tyd;
	private boolean backTesting=false;
	private String name;
	private HashSet<IndicatorInstanceName> openIndicators = new HashSet<IndicatorInstanceName>();
	
	public TeeYai(String name,TradeManager tm) {
		//write instantiation
		System.out.println("Create new Teeyai");
		monitors = new HashMap<FeedMonitorName,FeedMonitor>();
		charts = new HashMap<FeedMonitorName,ChartCreator>();
		feedMonitors = new HashMap<String,ArrayList<FeedMonitor>>();
		if(tm==null){
			this.tm = new TradeManager();
			this.tm.setTeeYaiName(name);
		} else {
			this.tm=tm;
			System.out.println("tm set");
		}
		this.name=name;
	}
	public FeedMonitor addFeed(FeedComponent feed,int interval,FeedMonitorName feedChart){
		
		//create price feed graph
		FeedMonitorName feedMonitorName = new FeedMonitorName(feed, interval);
		FeedMonitor fm=null;
		
		if(!monitors.containsKey(feedMonitorName)){
			ChartCreator chart = null; 
			boolean newChart = false;
			if(!charts.containsKey(feedChart)){
				newChart = true;
				chart = new BarAndLineChartCreator("Price",null,feedMonitorName);
				charts.put(feedMonitorName, chart);
			} else {
				chart = charts.get(feedChart);
			}
			
			
			fm = new FeedMonitor(chart,feedMonitorName,feed,interval,newChart);
			if(!feedMonitors.containsKey(feed.getName())){
				feedMonitors.put(feed.getName(), new ArrayList<FeedMonitor>());
				if(!backTesting){
					ServiceManager.getInstance().addFeedMonitor(feed.getName(), this);
				}
			}
			feedMonitors.get(feed.getName()).add(fm);
			monitors.put(feedMonitorName, fm);
		}
		//System.out.println("feed monitor setup complete");
		return fm;
	}
	public void setBroker(Broker broker){
		System.out.println("Setting broker "+(broker==null?"null":broker.getName()));
		tm.setBroker(broker);
	}
	public boolean addIndicator(FeedMonitorName feedmonitorname,Indicator indicator){

		if(monitors!=null){
			
			//System.out.println("Get feed monitor");
			//System.out.println("Indicator "+indicator.getIndicatorName());
			FeedMonitor fm = monitors.get(feedmonitorname);
			indicator.setFeedName(feedmonitorname);
			
			if(!openIndicators.contains(indicator.getInstanceName())){
				indicator.setInterval(fm.getInterval());
				if(indicator.hasOwnChart()){
					ChartCreator chart = indicator.createOwnChart();
					indicator.setChart(chart);
				} else {
					indicator.setChart(fm.getChart());
				}
	
				fm.addIndicator(indicator);
				tm.addIndicator(fm.getFeed().getName(),indicator);
				openIndicators.add(indicator.getInstanceName());
				return true;
			}

		}
		return false;
		
	}
	public void attach(TeeYaiListener tyd){
		this.tyd=tyd;
	}
	private void clearCharts(){
		Set<FeedMonitorName> keys = charts.keySet();
		Iterator<FeedMonitorName> chartNames = keys.iterator();
		while(chartNames.hasNext()){
			FeedMonitorName chartname= chartNames.next();
			ChartCreator c = charts.get(chartname);
			c.clearValues();
		}
	}
	public void reset(){

		tm.reset(false,true);
		clearCharts();
		clearMonitorValues();
	}
	
	private void clearMonitorValues(){
		Set<String> keys = feedMonitors.keySet();
		if(keys!=null&&keys.size()>0){
			Iterator<String> i = keys.iterator();
			while(i.hasNext()){
				clearMonitorValues(i.next());
			}
		}
	}
	public void backTest(String feedname,long start,long end){

		List<Price> prices = getPriceHistory(feedname,start,end);
		
		for(int i=0;i<prices.size();i++){
			Price price=prices.get(i);
			updatePrice(feedname, price);
		}
		if(tyd!=null){
			tyd.flushGraph();
		}
		
	}
	public void backTest(Date startDate,Date endDate){

		try {
			System.out.println("called backtest");

			SimpleDateFormat sdf = null;
			
			if(startDate!=null&&endDate!=null){
				
				/*if(start.length()==10&&end.length()==10){
					sdf = new SimpleDateFormat("dd/MM/yyyy");
				} else if(start.length()==16&&end.length()==16){
					sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				} else if(start.length()==19&&end.length()==19){
					sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				}
				sdf.setTimeZone(TimeZone.getTimeZone("GB"));
				Date startDate=sdf.parse(start);
				Date endDate=sdf.parse(end);*/
				System.out.println("range "+startDate+" "+endDate);
				
				reset();
				DateTime jstartDate = new DateTime(startDate);
				DateTime jendDate = new DateTime(endDate);
				int days = Days.daysBetween(jstartDate, jendDate).getDays();

				for(int day=0;day<=days;day++){
					DateTime dayStart = jstartDate.plusDays(day);
					DateTime dayEnd = jendDate.plusDays(day-days);
					
					tm.reset(false,false);
					clearMonitorValues();

					Set<String> feednames = feedMonitors.keySet();
					for(String feedname : feednames){
						System.out.println("feed "+feedname);
						backTest(feedname,dayStart.getMillis() , dayEnd.getMillis());
					}
				}
				
			}
		
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void destroy(){
		Iterator<FeedMonitorName> i = monitors.keySet().iterator();
		while(i.hasNext()){
			FeedMonitorName s = i.next();
			ServiceManager.getInstance().removeFeedMonitor(s.getFeedName(), this);
		}
		ServiceManager.getInstance().removeTeeYai(this);
	}
	
	public void detach(){
		tyd=null;
	}

	
	public ArrayList<Indicator> getIndicators(){
		
		ArrayList<Indicator> allIndicators = new ArrayList<Indicator>();
		if(monitors!=null){
			Set<FeedMonitorName> keys = monitors.keySet();
			for(FeedMonitorName key : keys){
				FeedMonitor fm = monitors.get(key);
				allIndicators.addAll(fm.getIndicators());
			}
		}
		return allIndicators;
	}
	public HashMap<FeedMonitorName, FeedMonitor> getMonitors() {
		return monitors;
	}

	public String getName() {
		return name;
	}
	public List<Price> getPriceHistory(String feedname,long start,long end){
		return ServiceManager.getInstance().getPriceHistory(feedname, start, end);
	}
	public TradeManager getTradeManager() {
		return tm;
	}
	public boolean hasFeed(FeedComponent feed,int interval){
		
		return monitors.containsKey(new FeedMonitorName(feed, interval));
		
	}
	
	public boolean hasFeed(FeedMonitorName feedMonitorName){
		
		return monitors.containsKey(feedMonitorName);
		
	}
	public boolean isBackTesting() {
		return backTesting;
	}
	public boolean isTradeManagerEnabled(){
		if(tm!=null){
			return tm.isTrading();
		}
		return false;
	}
	public void removeFeedMonitor(FeedMonitorName name){
		
		if(monitors.containsKey(name)){
			
			
			FeedMonitor fm = monitors.get(name);
			ArrayList<Indicator> inds = fm.getIndicators();
			if(inds!=null){
				for(Indicator ind : inds){
					tm.removeIndicator(fm.getFeed().getName(),ind);
					openIndicators.remove(ind.getInstanceName());
				}
				fm.removeIndicators();
			}
			feedMonitors.get(fm.getFeed().getName()).remove(fm);
			monitors.remove(name);
			if(feedMonitors.get(fm.getFeed().getName()).size()==0){
				feedMonitors.remove(fm.getFeed().getName());
				ServiceManager.getInstance().removeFeedMonitor(fm.getFeed().getName(), this);
			}
			
		}
		
	}
	public void removeIndicator(Indicator indicator){

		if(monitors!=null){
			
			FeedMonitor fm = monitors.get(indicator.getFeedName());
			if(fm!=null){
				fm.removeIndicator(indicator);
				tm.removeIndicator(fm.getFeed().getName(),indicator);
			}
			openIndicators.remove(indicator.getInstanceName());
		}
	}
	public void addStrategy(TradeStrategy ts){
		if(ts.getId()>0){
			ServiceManager.getInstance().updateTradeStrategy(ts);
			tm.updateStrategy(ts);
		} else {
			long id = ServiceManager.getInstance().addTradeStrategy(tm.getManagerID(), ts);
			ts.setId(id);
			tm.addStrategy(ts);
		}
	}
	public void deleteStrategy(TradeStrategy ts){
		if(ts!=null&&ts.getId()>0){
			ServiceManager.getInstance().deleteTradeStrategy(ts.getId());
			tm.deleteStrategy(ts);
		}
	}
	public void setBackTesting(boolean backTesting) {
		this.backTesting = backTesting;
	}
	public void setTradeManagerEnabled(boolean enabled){
		if(tm!=null){
			tm.setTrading(enabled);
		}
	}
	public void updateIndicatorOption(FeedMonitorName feedmonitorname,IndicatorInstanceName instanceName,String optionName,double value){
		FeedMonitor fm = monitors.get(feedmonitorname);

		ArrayList<Indicator> indList = fm.getIndicators();

		if(indList!=null&&instanceName!=null&&optionName!=null){

			for(Indicator i : indList){

				if(instanceName.equals(i.getInstanceName())){
					i.setOption(optionName, value);
				}
			}
		}
	}
	public void updatePrice(String name,Price price){
		
		//System.out.println("Price "+name+" is being updated "+price.getPrice());
		
		ArrayList<FeedMonitor> mons = feedMonitors.get(name);
		for(FeedMonitor m : mons){
			m.updatePrice(price);
		}
		tm.update(name,price);
		

		if(tyd!=null){
			//System.out.println("Updating display");
			tyd.updatePrice(name, price,isBackTesting());
			/*long max = Runtime.getRuntime().maxMemory();
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			if((total>.9*max)&&(free*100)/max<10){
				tyd.removeAndAddChartsToFreeMemory();
			}*/
		}
	}
	private void clearMonitorValues(String name){
		ArrayList<FeedMonitor> mons = feedMonitors.get(name);
		for(FeedMonitor m : mons){
			m.clearIndicatorValues();
		}
	}
	public void updateRecentTradeOnCharts(Position p){
		//ArrayList<Position> justExecuted = tm.getJustExecuted();
		//for(Position p : justExecuted){
			if(p!=null){
				System.out.println("position to chart "+(p.isLong()?"long":"short")+" "+(p.getClosed()==null?"open":"close"));
				Collection<ChartCreator> chartCreators = charts.values();
				for(ChartCreator cc : chartCreators){
					if(cc!=null){
						if(p.isLong()){
							if(p.getClosed()!=null){
								cc.updatePrice(new SimpleChartNameProvider("Sell"),  p.getClosed()==null?System.currentTimeMillis():p.getClosed().getTime(),p.getExitPoint(),false);
							} else {
								cc.updatePrice(new SimpleChartNameProvider("Buy"), p.getOpened()==null?System.currentTimeMillis(): p.getOpened().getTime(),p.getEntryPoint(),false);
							}
						} else {
							if(p.getClosed()!=null){
								cc.updatePrice(new SimpleChartNameProvider("Cover"), p.getClosed()==null?System.currentTimeMillis():p.getClosed().getTime(),p.getExitPoint(), false);
							} else {
								cc.updatePrice(new SimpleChartNameProvider("Short"), p.getOpened()==null?System.currentTimeMillis(): p.getOpened().getTime(),p.getEntryPoint(),false);
							}
						}
					}
				}
			}
		//}
	}
	
	public boolean isReadyToTrade(){
		return tm.isReadyToTrade();
	}

}
