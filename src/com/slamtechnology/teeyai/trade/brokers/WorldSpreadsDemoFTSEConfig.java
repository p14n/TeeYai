package com.slamtechnology.teeyai.trade.brokers;

import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.trade.TradeConfiguration;

public class WorldSpreadsDemoFTSEConfig extends TradeConfiguration {

	public WorldSpreadsDemoFTSEConfig() {
		super();
		targetProfit = 0;
		profitTake = 0;
		amount = 1;
		spread=1;
		stopSpread=0;
		stop=0;
		//lobsterLevel=3;
		maxTrades=3;
		openingTime = "8:00";
		closingTime = "16:30";
		minSwing=0;
		openingHour=8;
		openingMin=0;
		closingHour=16;
		closingMin=30;
		priceTolerance=1.0;
		broker = ServiceManager.getInstance().getBrokers()[0];
	}

	@Override
	public String toString() {
		return "World spreads demo FTSE";
	}

}
