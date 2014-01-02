package com.slamtechnology.teeyai.indicators;

import org.apache.tools.ant.types.selectors.DifferentSelector;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.SimpleChartNameProvider;

public class MACDReversalLimit extends MACDIndicator {
	
	Integer lastDirection = null;
	int maxReversals = 2;
	int reversalCount = 0;
	double significantChange = 0.01;
	
public MACDReversalLimit(){
	setIndicatorName("MACDR");
	setHasOwnChart(true);
	setup();
	setDecisionIndicators();
}

@Override
public String toString() {
	return "MACD Reversal Limit";
}

@Override
public Indicator getNewInstance() {
	return new MACDReversalLimit();
}

@Override
protected void update() {
	double last = lastValue;
	super.update();
	if(lastDirection!=null&&MACDmarket!=0){
		if(lastDirection+MACDmarket==0){
			if(Math.abs(getValue()-last)>=significantChange){
				reversalCount++;
				lastDirection = MACDmarket;
				if(getChart()!=null){
					getChart().updatePrice(
							new SimpleChartNameProvider("Reversal"),
							getLatestPrice().getTime(),getValue(),false);
				}
			}
		}
	}
	if(lastDirection==null&&MACDmarket!=0){
		lastDirection = MACDmarket;
	}
	setDecisionIndicators();
}
private void setDecisionIndicators(){
	boolean canTrade = reversalCount<=maxReversals;
	setBuy(canTrade);
	setSell(canTrade);
	setShort(canTrade);
	setCover(canTrade);
}
	
	@Override
	protected void clearInternalValues() {
		reversalCount = 0;
		lastDirection = null;
		super.clearInternalValues();
	}

}
