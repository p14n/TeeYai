package com.slamtechnology.teeyai;

import java.util.ArrayList;

import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;

public class FeedMonitor {
	
	ArrayList<Indicator> indicators;
	
	ChartCreator chart;
	
	boolean running = true;
	int interval;
	int intervalCount;
	FeedComponent feed;
	FeedMonitorName name;
	Price latestPrice;
	boolean ownChart;
	
	public FeedComponent getFeed() {
		return feed;
	}


	public FeedMonitorName getName() {
		return name;
	}

	public int indicatorCount(){
		return indicators.size();
	}
	
	public FeedMonitor(ChartCreator chart,FeedMonitorName name,FeedComponent feed, int interval,boolean ownChart){
		
		this.ownChart=ownChart;
		this.interval=interval;
		resetIntervalCount();
		indicators = new ArrayList<Indicator>();
		this.feed = feed;
		this.name=name;
		this.chart=chart;
		
	}
		
	
	public void addIndicator(Indicator indicator){
		if(indicators==null){
			indicators = new ArrayList<Indicator>();
		}
		indicators.add(indicator);

	}
	public void removeIndicator(Indicator indicator){
		if(indicators!=null){
			indicators.remove(indicator);
		}
	}
	public void removeIndicators(){
		if(indicators!=null){
			indicators.removeAll(indicators);
		}
	}
	public ArrayList<Indicator> getIndicators(){
		return indicators;
	}

	public int getInterval() {
		return interval;
	}
	
	/*public void onMessage(Message arg0) {
		
		double price;
		try {
			price = Double.parseDouble(((TextMessage)arg0).getText());
			System.out.println("got price "+price);
			Price p = new Price();
			p.setPrice(price);
			p.setTime(System.currentTimeMillis());
			updatePrice(p);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	private void resetIntervalCount(){
		intervalCount=interval-1;
		
	}
	public void updatePrice(Price price){
		
		//System.out.print("Feedmonitor "+name+" update price ");
		if(chart!=null){
			if(latestPrice==null)latestPrice = price;
			if(intervalCount==0){
				chart.updatePrice(name, price.getTime(), new double[]{price.getPrice()},true);
				latestPrice= price;
				resetIntervalCount();
			} else {
				intervalCount--;
			}
		}
		//System.out.println(" latest price "+(latestPrice==null?0:latestPrice.getPrice()));

		
		//System.out.println("Feedmonitor "+name+" updating indicators");
		for(int indicatorCount=0;indicatorCount<indicators.size();indicatorCount++){
			
			Indicator indicator = indicators.get(indicatorCount);
			//System.out.print("Updating "+indicator.getInstanceName());
			indicator.update(price);
			//System.out.print("value "+indicator.getValue()+" for price "+price.getPrice());
			//System.out.println(" short "+indicator.isShort());
			
		}
	}
	public void clearIndicatorValues(){
		
		resetIntervalCount();

		for(int indicatorCount=0;indicatorCount<indicators.size();indicatorCount++){
			
			Indicator indicator = indicators.get(indicatorCount);
			indicator.clearValues();
			
		}
	}
	public void updatePrice(Price[] prices){
		
		for(Price price:prices){
			
			updatePrice(price);
			
		}
	}


	public Price getLatestPrice() {
		return latestPrice;
	}


	public ChartCreator getChart() {
		return chart;
	}


	public boolean hasOwnChart() {
		return ownChart;
	}
}
