package com.slamtechnology.teeyai;

import java.util.HashMap;
import java.util.List;

import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;
import com.slamtechnology.teeyai.prices.Price;

public abstract class Indicator {
	
	double value;
	String indicatorName;
	IndicatorInstanceName instanceName;
	FeedMonitorName feedName;
	int interval;
	Price latestPrice;
	HashMap<String, IndicatorOption> options;
	ChartCreator chart;
	
	private int intervalCount;


	boolean buy;
	boolean sell;
	boolean shortTrade;
	boolean cover;
	
	private boolean hasOwnChart=false;
	
	protected void resetIntervalCount(){
		intervalCount=0;
	}
	protected void stepIntervalCount(){
		intervalCount++;
	}
	protected boolean atInterval(){
		return intervalCount==getInterval()-1;
	}

	
	public void clearValues(){
		value=0;
		latestPrice=null;
		buy = false;
		sell = false;
		shortTrade = false;
		cover = false;
		clearInternalValues();
	}
	protected abstract void clearInternalValues();
	
	public String getIndicatorName() {
		return indicatorName;
	}
	public IndicatorInstanceName getInstanceName() {
		return instanceName;
	}
	public FeedMonitorName getFeedName() {
		return feedName;
	}
	public void setFeedName(FeedMonitorName feedName) {
		this.feedName = feedName;
		this.instanceName = new IndicatorInstanceName(feedName, this);
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public double getValue() {
		return value;
	}
	public boolean isBuy() {
		return buy;
	}
	public boolean isSell() {
		return sell;
	}
	public boolean isShort() {
		return shortTrade;
	}
	public boolean isCover() {
		return cover;
	}
	public void update(Price price){
		latestPrice = price;
		update();
	}
	protected abstract void update();
	public void update(List<Price> prices){
		if(prices!=null){
			for(Price p : prices){
				update(p);
			}
		}
	}
	public abstract Indicator getNewInstance();
	protected void setValue(double price) {
		this.value = price;
	}

	protected void setBuy(boolean buy) {
		this.buy = buy;
	}
	protected void setSell(boolean sell) {
		this.sell = sell;
	}
	protected void setShort(boolean shortTrade) {
		this.shortTrade = shortTrade;
	}
	protected void setCover(boolean cover) {
		this.cover = cover;
	}
	public HashMap<String, IndicatorOption> getOptions() {
		return options;
	}
	public void setOption(String name, double value) {
		IndicatorOption option = options.get(name);
		option.setValue(value);
		optionUpdated(option.getName());
	}
	public void addOption(IndicatorOption option) {
		if(options==null){
			options = new HashMap<String, IndicatorOption>();
		}
		options.put(option.getName(), option);
		optionUpdated(option.getName());
	}
	protected abstract void optionUpdated(String name);
	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}
	public Price getLatestPrice() {
		return latestPrice;
	}
	public ChartCreator getChart() {
		return chart;
	}

	public void setChart(ChartCreator chart) {
		this.chart = chart;
	}
	public boolean hasOwnChart() {
		return hasOwnChart;
	}
	public void setHasOwnChart(boolean hasOwnChart) {
		this.hasOwnChart = hasOwnChart;
	}
	protected void updateChart(final ChartNameProvider datasetName,final long time,final double price,boolean allowZero){
		updateChart(datasetName, time, new double[]{price},allowZero);
	}
	protected void updateChart(final ChartNameProvider datasetName,final long time,final double price){
		updateChart(datasetName, time, new double[]{price},false);
	}
	protected void updateChart(final ChartNameProvider datasetName,final long time,final double[] price,boolean allowZero){
		final ChartCreator chart = getChart();
		if(chart!=null&&price!=null&&price.length>0&&(allowZero||price[0]!=0)){
			new Thread(new Runnable() {
				public void run() {
						chart.updatePrice(datasetName, time, price);
				}
			}).start();
		}
	}
	
	public ChartCreator createOwnChart(){
		return new BarAndLineChartCreator(getIndicatorName(),null,getInstanceName());
	}

}
