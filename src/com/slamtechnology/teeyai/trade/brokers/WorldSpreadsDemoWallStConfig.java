package com.slamtechnology.teeyai.trade.brokers;

import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.trade.TradeConfiguration;

public class WorldSpreadsDemoWallStConfig extends TradeConfiguration {

	public WorldSpreadsDemoWallStConfig() {
		super();
		targetProfit = 0;
		profitTake = 0;
		amount = 1;
		spread=2;
		stopSpread=15.0;
		stop=0;
		//lobsterLevel=3;
		maxTrades=1;
		openingTime = "15:30";
		closingTime = "16:30";
		minSwing=0;
		openingHour=15;
		openingMin=30;
		closingHour=16;
		closingMin=30;
		priceTolerance = 1.0;
		broker = ServiceManager.getInstance().getBrokers()[1];
	}
	@Override
	public String toString() {
		return "World spreads demo Wall st";
	}


}
