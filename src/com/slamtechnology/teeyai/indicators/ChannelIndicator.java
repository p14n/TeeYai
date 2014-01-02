package com.slamtechnology.teeyai.indicators;

import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;

public class ChannelIndicator extends Indicator {
	
	Stack<Double> differences;
	TreeMap<Double,Integer> sortedDifferences;
	int valueCount;
	double channelHeight;
	EMAIndicator emaIn;
	boolean firstUpdate=true;
	IndicatorOption valsToMon;
	IndicatorOption channelCoefficient;
	double highChannel;
	double lowChannel;
	
	public ChannelIndicator(){
		
		setIndicatorName("EMAChannel");
		valsToMon = new IndicatorOption(IndicatorOption.VALS_TO_MONITOR,"Values to monitor",22);
		emaIn = new EMAIndicator();
		addOption(valsToMon);
		channelCoefficient = new IndicatorOption(IndicatorOption.CHANNEL_TRADE_COEFFICIENT,"Channel trade coefficient",1);
		addOption(channelCoefficient);

		valueCount=0;
		differences = new Stack<Double>();
		sortedDifferences = new TreeMap<Double,Integer>();
		channelHeight=0;
	}
	public String toString(){
		return "EMA Channel";
	}

	@Override
	public Indicator getNewInstance() {
		// TODO Auto-generated method stub
		return new ChannelIndicator();
	}

	@Override
	protected void update() {
		
		if(firstUpdate){
			emaIn.setInterval(getInterval());
			firstUpdate = false;
		}
		emaIn.update(getLatestPrice());
		double price = getLatestPrice().getPrice();

		double ema = emaIn.getValue();
		//work out difference between ema and value
		double difference = Math.abs(price - ema);
		
		//add to sorted list of values
		Double differenceObj = new Double(difference);
		Integer newCount;
		if(sortedDifferences.containsKey(differenceObj)){
			int curr = sortedDifferences.get(differenceObj).intValue();
			newCount = new Integer(curr+1);
		} else {
			newCount = new Integer(1);
		}
		sortedDifferences.put(differenceObj, newCount);
		differences.push(differenceObj);
		
		//increment value counter
		if(valueCount>=(valsToMon.getValue()*getInterval())){
			//remove from values
			Double oldVal = differences.pop();
			int curr = sortedDifferences.get(oldVal).intValue();
			if(curr>1){
				Integer newVal = new Integer(curr-1);
				sortedDifferences.put(oldVal,newVal);
			} else {
				sortedDifferences.remove(oldVal);
			}
		} else {
			valueCount++;
		}
		//step down the set until 5% of prices have gone past
		Set<Double> keys = sortedDifferences.keySet();
		int channelCount = 0;
		for(Double d: keys){
			channelCount = + sortedDifferences.get(d).intValue();
			if(channelCount<(valueCount*.95)){
				//use this as the new channel height
				channelHeight = d.doubleValue();
			}
		}
		setValue(channelHeight);
		highChannel = ema+channelHeight;
		lowChannel = ema-channelHeight;
		
		double tradePoint = channelHeight*channelCoefficient.getValue();
		
		if(emaIn.isBullMarket()){
			
			if(price>(ema+tradePoint)){
				setBuy(false);
				setSell(true);
				setShort(false);
				setCover(false);
			} else if(price<(ema-tradePoint)){
				setBuy(true);
				setSell(false);
				setShort(false);
				setCover(false);
			}
			
		} else {
			
			if(price>(ema+tradePoint)){
				setBuy(false);
				setSell(false);
				setShort(true);
				setCover(false);
			} else if(price<(ema-tradePoint)){
				setBuy(false);
				setSell(false);
				setShort(false);
				setCover(true);
			}

		}
		
	}

	@Override
	protected void optionUpdated(String name) {
		if(IndicatorOption.VALS_TO_MONITOR.equals(name)){
			emaIn.setOption(IndicatorOption.VALS_TO_MONITOR, valsToMon.getValue());
		}
	}

	public double getHighChannel() {
		return highChannel;
	}

	public double getLowChannel() {
		return lowChannel;
	}
	@Override
	protected void clearInternalValues() {
		// TODO Auto-generated method stub
		
	}

}
