package com.slamtechnology.teeyai.trade;

public class TradeConfiguration {

	protected Broker broker;
	protected double spread;
	protected double amount=1;
	protected double targetProfit=12;
	protected double stopSpread=6.0;
	protected double stop=0;
	protected double profitTake=12;
	protected double priceTolerance=0;
	protected double lobsterLevel=3;
	protected int maxTrades=-1;
	protected String openingTime = "9:30";
	protected String closingTime = "10:00";
	protected int minSwing=0;
	protected int openingHour=9;
	protected int openingMin=30;
	protected int closingHour=10;
	protected int closingMin=00;
	public Broker getBroker() {
		return broker;
	}
	

	
}
