package com.slamtechnology.teeyai.indicators;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.util.LogSetup;

public class EMAIndicator extends Indicator {
	

	long valuesMonitored;
	long valuesToMonitorBeforeOpinion;
	double ema;

	double k;
	private int market;

	boolean indPrice=true;
	boolean indTrend=true;
	double lastEma = 0;
	double roundtoDP = 100;
	IndicatorOption valsToMon;
	IndicatorOption indicatePrice;
	IndicatorOption indicateTrend;
	IndicatorOption rounding;
	boolean priceIsAboveLine=false;
	boolean priceWasAboveLine=false;
	Logger logger = LogSetup.indicatorLog;
	double sma;
	
	LinkedList<Double> lastPrices;

	public EMAIndicator(){
		
		setIndicatorName("EMA");

		valsToMon = new IndicatorOption(IndicatorOption.VALS_TO_MONITOR,"Values to monitor",20);
		indicatePrice = new IndicatorOption(IndicatorOption.INDICATE_PRICE,"Indicate price",0);
		indicateTrend = new IndicatorOption(IndicatorOption.INDICATE_TREND,"Indicate trend",1);
		rounding = new IndicatorOption(IndicatorOption.ROUNDING,"Rounding to dp",1);
		addOption(valsToMon);
		addOption(indicatePrice);
		addOption(indicateTrend);
		addOption(rounding);
		resetIntervalCount();
		ema=0;
		k = 2 / (valsToMon.getValue() + 1);
		market=0;
		lastPrices = new LinkedList<Double>();
		logger.info("EMA creation buy "+isBuy()+" sell "+isSell()+" short "+isShort()+" cover "+isCover());

	}
	public String toString(){
		return "Exponential moving average";
	}

	
	@Override
	public Indicator getNewInstance() {
		return new EMAIndicator();
	}
	
	private boolean calculateSMA(){
		double valsToMonP = valsToMon.getValue();
		if(lastPrices.size()<valsToMonP){
			if(getLatestPrice().getPrice()!=0)
				lastPrices.add(getLatestPrice().getPrice());
			sma = 0;
			for(Double p:lastPrices){
				sma = p + sma;
			}
			sma = sma/lastPrices.size();
			return true;
		}
		return false;
	}
/*
	private void resetIntervalCount(){
		intervalCount=getInterval()-1;
	}
	private void stepIntervalCount(){
		intervalCount--;
	}
	private boolean atInterval(){
		return intervalCount==0;
	}*/
	@Override
	protected void update() {
		
		if(valuesMonitored<Long.MAX_VALUE-1){
			valuesMonitored++;
		}
		
		
		Price p = getLatestPrice();
		
		boolean intervalPrice = false;
		if(atInterval()){
			
			intervalPrice = true;
			
			boolean useSMA = calculateSMA();
			if(sma!=0){
				double close = getLatestPrice().getPrice();
				if(useSMA){
					ema = sma;
				} else {
					ema = (close * k) + ( ema * (1-k));
				}
				//ema = ((close - lastEma)*k) + lastEma;
			}

			
			setValue(ema);
			resetIntervalCount();
		} else {
			stepIntervalCount();
		}
		double roundedEMA = 0;
		
		if(roundtoDP!=-1){
			roundedEMA = Math.round(ema*roundtoDP)/roundtoDP;
		} else {
			roundedEMA = ema;
		}
		
		if(lastEma==0){
			lastEma=roundedEMA;
		}
		String output = " Ema ("+getInterval()+") :"+roundedEMA+" last ema:"+lastEma;
		
		if(valuesMonitored>=valuesToMonitorBeforeOpinion){
			if(roundedEMA>lastEma){ //current bull
				market=1;
			} else if (roundedEMA<lastEma){
				market=-1;
			} else if (intervalPrice){
				market=0;
			}
		} else {
			output = output + " NO OPINION "+valuesMonitored;
		}
		
		
		lastEma = roundedEMA;
		output = output + " market:"+market+" price "+p.getPrice();
		
		if(indPrice&&indTrend){
			if(market==1){
				
				if(p.getPrice()>ema){
					setBuy(false);
					setSell(true);
					setShort(false);
					setCover(false);
				} else {
					setBuy(true);
					setSell(false);
					setShort(false);
					setCover(false);
				}
				
			} else if (market==-1){
				
				if(p.getPrice()>ema){
					setBuy(false);
					setSell(false);
					setShort(true);
					setCover(false);
				} else {
					setBuy(false);
					setSell(false);
					setShort(false);
					setCover(true);				
				}
			} else {
				setBuy(false);
				setSell(false);
				setShort(false);
				setCover(false);				
				
			}
		} else if(indPrice){
			if(p.getPrice()>ema){
				if(priceWasAboveLine){
					setBuy(false);
					setSell(false);
					setShort(false);
					setCover(false);
				} else {
					setBuy(true);
					setSell(false);
					setShort(false);
					setCover(true);
				}
				priceWasAboveLine = true;
			} else if (p.getPrice()<ema) {
				if(priceWasAboveLine){
					setBuy(false);
					setSell(true);
					setShort(true);
					setCover(false);
				} else {
					setBuy(false);
					setSell(false);
					setShort(false);
					setCover(false);
				}
				priceWasAboveLine = false;
			}
			
		} else if(indTrend){
			if(market==1){
				setBuy(true);
				setSell(false);
				setShort(false);
				setCover(true);				
			} else if(market==-1){
				setBuy(false);
				setSell(true);
				setShort(true);
				setCover(false);
			} else {
				setBuy(false);
				setSell(false);
				setShort(false);
				setCover(false);
			}
		}
		
		logger.info(output+" buy "+isBuy()+" sell "+isSell()+" short "+isShort()+" cover "+isCover());

		updateChart(getInstanceName(), p.getTime(), getValue());

	}
	


	protected boolean isBullMarket() {
		return market==1;
	}
	protected boolean isBearMarket() {
		return market==-1;
	}


	@Override
	protected void optionUpdated(String name) {
		k = 2 / (valsToMon.getValue() + 1);
		indPrice = indicatePrice.getValue()==1;
		indTrend = indicateTrend.getValue()==1;

		if(rounding.getValue()==-1){
			roundtoDP = -1;
		} else {
			roundtoDP = Math.pow(10,rounding.getValue());
		}


	}

	@Override
	protected void clearInternalValues() {
		resetIntervalCount();
		ema=0;
		market=0;
		lastEma = 0;
		valuesMonitored=0;
		lastPrices.clear();
	}


}
