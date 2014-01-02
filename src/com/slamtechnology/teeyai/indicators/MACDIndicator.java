package com.slamtechnology.teeyai.indicators;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.util.LogSetup;

public class MACDIndicator extends Indicator {
	
	EMAIndicator highFastEMA;
	EMAIndicator lowFastEMA;
	EMAIndicator slowEMA;
	boolean firstRun=true;
	
	Logger log = LoggerFactory.getLogger(MACDIndicator.class.getName());
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm");
	
	double fastLine;
	double slowLine;
	double lastFastLine;
	double lastValue;
	boolean wasOverTheLine;
	int MACDmarket = 0;
	long lastPriceAt=0;

	public MACDIndicator(){
		setIndicatorName("MACD");
		setHasOwnChart(true);
		setup();
	}
	
	protected void setup(){
		MACDmarket=0;
		highFastEMA = new EMAIndicator();
		highFastEMA.setOption(IndicatorOption.VALS_TO_MONITOR, 26);
		highFastEMA.setOption(IndicatorOption.ROUNDING, -1);
		//highFastEMA.setLog(false);
		lowFastEMA = new EMAIndicator();
		lowFastEMA.setOption(IndicatorOption.VALS_TO_MONITOR, 12);
		lowFastEMA.setOption(IndicatorOption.ROUNDING, -1);
		//lowFastEMA.setLog(false);
		slowEMA = new EMAIndicator();
		slowEMA.setOption(IndicatorOption.VALS_TO_MONITOR, 7);
		slowEMA.setOption(IndicatorOption.ROUNDING, -1);
		//slowEMA.setLog(false);
	}
	
	public String toString(){
		return "MACD Histogram";
	}
	@Override
	public Indicator getNewInstance() {
		return new MACDIndicator();
	}

	@Override
	protected void optionUpdated(String name) {
	}
private void setMarketDirection(){
		
		lastPriceAt = getLatestPrice().getTime();
		if(getValue()!=0&&lastValue!=getValue()){
			MACDmarket = 0;
			if(lastValue<getValue()&&getValue()>0){
				MACDmarket = 1;
			} else if(lastValue>getValue()&&getValue()<0){
				MACDmarket = -1;
			}
		}

	}
	/*private void setMarketDirection(){
		
		boolean significantChange = true;
		if(lastPriceAt>0){
			long msBetweenPrices = getLatestPrice().getTime() - lastPriceAt;
			double valBetweenPrices = lastValue - getValue();
			log.info(getInstanceName().getName() 
					+ " price "+ getLatestPrice().getPrice() 
					+ " day/time " + sdf.format(new Date(getLatestPrice().getTime()))
					+ " last value " + lastValue
					+ " this value " + getValue() 
					+ " difference " + valBetweenPrices
					+ " time between " + msBetweenPrices
					+ " time over diff " + (msBetweenPrices/valBetweenPrices)
					+ " diff over time " + (valBetweenPrices/msBetweenPrices));
			
			double timeOverDiff = msBetweenPrices/valBetweenPrices;
			if(timeOverDiff<0) timeOverDiff = timeOverDiff * -1;
			if(timeOverDiff>100000.0){
				significantChange = false;
			}
			
			if(valBetweenPrices!=0.0&&Math.abs(valBetweenPrices)<0.01){
				significantChange = false;
			}
		}
		lastPriceAt = getLatestPrice().getTime();
		
		if(getValue()!=0){
			if(lastValue<getValue()){
				if(!significantChange&&MACDmarket==-1){
					MACDmarket = 0;
				} else {
					MACDmarket=1;
				}
				MACDmarket=1;
			} else if(lastValue>getValue()){
				if(!significantChange&&MACDmarket==1){
					MACDmarket = 0;
				} else {
					MACDmarket=-1;
				}
				MACDmarket=-1;
			}
		}

	}*/

	@Override
	protected void update() {
		Price price = getLatestPrice();
		if(firstRun){
			highFastEMA.setInterval(getInterval());
			lowFastEMA.setInterval(getInterval());
			slowEMA.setInterval(getInterval());
			firstRun = false;
		}
		highFastEMA.update(price);
		lowFastEMA.update(price);
		
		/*
		 * 
MACD: (12-day EMA - 26-day EMA) 

Signal Line: 9-day EMA of MACD

MACD Histogram: MACD - Signal Line
		 */
		if(highFastEMA.getValue()>0){
			fastLine = lowFastEMA.getValue() - highFastEMA.getValue();
			
			Price fastPrice = new Price();
			fastPrice.setTime(price.getTime());
			fastPrice.setPrice(fastLine);
			
			slowEMA.update(fastPrice);
			slowLine = slowEMA.getValue();
			
			log.info("high {} low {} slow {} price {} fastprice {} price date {}",new Object[]{
					highFastEMA.getValue(),
					lowFastEMA.getValue(),
					slowEMA.getValue(),price.getPrice(),fastLine,new Date(price.getTime())}
					);
			
			//if(slowLine>0){

				setValue(fastLine-slowLine);
				
				setMarketDirection();

				boolean isOverTheLine = getValue()>0;
				lastFastLine = fastLine;
				lastValue = getValue();
				
				if(MACDmarket==1){//&&!isOverTheLine){
					
					setSell(false);
					setShort(false);
					setCover(true);
					setBuy(true);
					
				} else if(MACDmarket==-1){//&&isOverTheLine){
					setBuy(false);
					setSell(true);
					setCover(false);
					setShort(true);
				} else {
					setBuy(false);
					setSell(false);
					setCover(false);
					setShort(false);
				}

				wasOverTheLine = isOverTheLine;
				//if(atInterval()){
					updateChart(getInstanceName(), price.getTime(), getValue(),true);
				//}

			//}
			
			
		}
		if(atInterval()){
			resetIntervalCount();
		} else {
			stepIntervalCount();
		}


	}

	@Override
	protected void clearInternalValues() {
		firstRun=true;
		fastLine=0;
		slowLine=0;
		lastFastLine=0;
		lastValue=0;
		wasOverTheLine = false;
		MACDmarket = 0;
		highFastEMA.clearValues();
		lowFastEMA.clearValues();
		slowEMA.clearValues();
	}

}
