package com.slamtechnology.teeyai;

public class IndicatorOption {
	
	public static final String VALS_TO_MONITOR = "valsToMon";
	public static final String INDICATE_PRICE = "indPrice";
	public static final String INDICATE_TREND = "indTrend";
	public static final String ROUNDING = "rounding";
	public static final String CHANNEL_TRADE_COEFFICIENT = "chanTradCo";
	public static final String SIGNAL_DISTANCE_FROM_MIDPOINT = "signalFromMid";
	
	private String name;
	private String description;
	private double value;
	
	public IndicatorOption(String name,String description,double value){
		this.name = name;
		this.description = description;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String toString(){
		return getDescription();
	}

}
