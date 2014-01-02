package com.slamtechnology.teeyai.indicators;

import java.util.LinkedList;
import java.util.Stack;
import static java.lang.Math.*;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.prices.Price;

public abstract class ADXIndicator extends Indicator {
	
	//http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:average_directional_
	
	private class Period {
		double high = 0;
		double low = 0;
		double close = 0;
		double TR = 0;
		double plusDM = 0;
		double minusDM = 0;
		double TR14 = 0;
		double plusDM14 = 0;
		double minusDM14 = 0;
		double plusDI14 = 0;
		double minusDI14 = 0;
		double DX = 0;
		double ADX = 0;
	}
	ChartNameProvider threshold;
	ChartNameProvider increase;
	int periodsToCount=14;
	public void setPeriodsToCount(int periodsToCount) {
		this.periodsToCount = periodsToCount;
	}

	public void setValuesPerPeriod(int valuesPerPeriod) {
		this.valuesPerPeriod = valuesPerPeriod;
	}
	int valuesPerPeriod = 6;
	
	int valuesInThisPeriod=0;
	int levelAtWhichMarketHasDirection=30;
	
	Period current = new Period();
	Period previous = null;
	LinkedList<Period> periods = new LinkedList<Period>();
	double difference;
	double multiplier;

	public ADXIndicator(String indicatorName,double requiredIncreasePercentage) {
		super();
		this.multiplier = 3000/requiredIncreasePercentage;
		setHasOwnChart(true);
		setIndicatorName(indicatorName);
		setBuy(false);
		setShort(false);
		setSell(true);
		setCover(true);
		threshold = new ChartNameProvider() {
			public String getChartName() {
				return "ADX threshold";
			}
		};
		increase = new ChartNameProvider() {
			public String getChartName() {
				return "ADX Increase";
			}
		};
	}

	@Override
	protected void clearInternalValues() {
		valuesInThisPeriod=0;
		current = new Period();
		previous = null;
		periods = new LinkedList<Period>();
	}

	@Override
	protected void update() {
		Price p = getLatestPrice();
		if(valuesInThisPeriod==valuesPerPeriod){
			if(periods.size()>0)
				calculateCurrentADX();
			updateGraph();
			valuesInThisPeriod=0;
			periods.add(current);
			if(periods.size()>periodsToCount){
				periods.remove();
			}
			current = new Period();
		}
		current.close = p.getPrice();
		if(current.high<p.getPrice()) current.high = p.getPrice();
		if(current.low==0||current.low>p.getPrice()) current.low = p.getPrice();
		valuesInThisPeriod++;
	}

	private void updateGraph() {
			updateChart(getInstanceName(), getLatestPrice().getTime(), getValue());
			updateChart(threshold, getLatestPrice().getTime(), levelAtWhichMarketHasDirection);
			if(difference<0) difference = 0;
			updateChart(increase, getLatestPrice().getTime(), difference);
	}
	
	private double to2dp(double d){
		d = d * 100;
		d = Math.round(d);
		return d/100;
	}

	private void calculateCurrentADX() {
		Period previous = periods.getLast();
		//Current High less the current Low
		double highLessLow = current.high-current.low;
		//Current High less the previous Close (absolute value)
		double highLessClose = abs(current.high-previous.close);
		//Current Low less the previous Close (absolute value)
		double lowLessClose = abs(current.low-previous.close);
		
		current.TR = to2dp(max(max(highLessLow, highLessClose),lowLessClose));

		double previousLowLessCurrentLow = previous.low-current.low;
		double currentHighLessPreviousHigh = current.high-previous.high;

		//Directional movement is positive (plus) when the current high minus the prior 
		//high is greater than the prior low minus the current low. This so-called Plus 
		//Directional Movement (+DM) then equals the current high minus the prior high, 
		//provided it is positive. A negative value would simply be entered as zero.
		if(currentHighLessPreviousHigh>previousLowLessCurrentLow
				&&currentHighLessPreviousHigh>0){
			current.plusDM = to2dp(currentHighLessPreviousHigh);
		}

		//Directional movement is negative (minus) when the prior low minus the current 
		//low is greater than the current high minus the prior high. This so-called Minus 
		//Directional Movement (-DM) equals the prior low minus the current low, provided 
		//it is positive. A negative value would simply be entered as zero.
		if(previousLowLessCurrentLow>currentHighLessPreviousHigh
				&&previousLowLessCurrentLow>0){
			current.minusDM = to2dp(previousLowLessCurrentLow);
		}
		
		if(periods.size()==periodsToCount){
			if(previous.TR14==0){
				//First TR14 = Sum of first 14 periods of TR1
				current.TR14 = current.TR; 
				current.plusDM14 = current.plusDM;
				current.minusDM14 = current.minusDM;
				
				for(Period p:periods){
					current.TR14 = to2dp(current.TR14 + p.TR); 
					current.plusDM14 = to2dp(current.plusDM14 + p.plusDM);
					current.minusDM14 = to2dp(current.minusDM14 + p.minusDM);
				}
			} else {
				//Subsequent Values = Prior TR14 - (Prior TR14/14) + Current TR14
				current.TR14 = to2dp(previous.TR14 - (previous.TR14/periodsToCount) + current.TR);
				current.plusDM14 = to2dp(previous.plusDM14 - (previous.plusDM14/periodsToCount) + current.plusDM);
				current.minusDM14 = to2dp(previous.minusDM14 - (previous.minusDM14/periodsToCount) + current.minusDM);
			}
			
			/* Divide the 14-day smoothed Plus Directional Movement (+DM) by the 14-day smoothed True Range 
			 * to find the 14-day Plus Directional Indicator (+DI14). Multiply by 100 to move the decimal point 
			 * two places. This +DI14 is the Plus Directional Indicator (green line) that is plotted along with ADX.*/
			current.plusDI14 = to2dp(current.plusDM14 / current.TR14 * 100);

			/* Divide the 14-day smoothed Minus Directional Movement (-DM) by the 14-day smoothed True Range to find
			 * the 14-day Minus Directional Indicator (-DI14). Multiply by 100 to move the decimal point two places. 
			 * This -DI14 is the Minus Directional Indicator (red line) that is plotted along with ADX.*/
			current.minusDI14 = to2dp(current.minusDM14 / current.TR14 * 100);

			/* The Directional Movement Index (DX) equals the absolute value of +DI14 less - DI14 divided by the sum of +DI14 and - DI14.*/
			double DI14Diff = abs(current.plusDI14-current.minusDI14);
			double DI14Sum = current.plusDI14+current.minusDI14;
			current.DX = to2dp((100 * DI14Diff) / DI14Sum);

			/* After all these steps, it is time to calculate the Average Directional Index (ADX). The first ADX value 
			 * is simply a 14-day average of DX. Subsequent ADX values are smoothed by multiplying the previous 14-day ADX 
			 * value by 13, adding the most recent DX value and dividing this total by 14.*/
			if(previous.ADX>0){
				current.ADX = to2dp(((previous.ADX * (periodsToCount-1)) + current.DX)/periodsToCount);
			} else if (periods.get(1).DX>0){
				double totalDX = current.DX;
				for(Period p:periods){
					totalDX += p.DX;
				}
				current.ADX = to2dp(totalDX/periodsToCount);
			}
			double last = getValue();
			setValue(current.ADX);
			if(last>0){
				difference = (getValue()-last)*multiplier;
			}
			if(difference>70) difference=70;
			if(current.ADX>levelAtWhichMarketHasDirection&&difference>=30){
			//if(current.ADX>levelAtWhichMarketHasDirection&&current.ADX>last){
				setBuy(true);
				setShort(true);
			} else {
				setBuy(false);
				setShort(false);
			}
		}
		
		
	}


	@Override
	public String toString() {
		return getIndicatorName();
	}
}
