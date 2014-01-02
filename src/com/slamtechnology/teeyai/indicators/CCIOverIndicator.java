package com.slamtechnology.teeyai.indicators;

import java.util.LinkedList;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.prices.Price;
import static java.lang.Math.*;

public abstract class CCIOverIndicator extends Indicator {
	
	private class Period {
		double high = 0;
		double low = 0;
		double close = 0;
		double TP=0;
	}
	
	ChartNameProvider highThreshold;
	ChartNameProvider lowThreshold;
	
	int periodsToCount=20;
	int valuesPerPeriod = 6;
	int valuesInThisPeriod=0;
	Period current = new Period();
	LinkedList<Period> periods = new LinkedList<Period>();
	boolean includeDescendingInOverBought = false;
	
	public void setPeriodsToCount(int periodsToCount) {
		this.periodsToCount = periodsToCount;
	}

	public void setValuesPerPeriod(int valuesPerPeriod) {
		this.valuesPerPeriod = valuesPerPeriod;
	}

	@Override
	protected void clearInternalValues() {
		valuesInThisPeriod=0;
		current = new Period();
		periods = new LinkedList<Period>();

	}

	@Override
	protected void update() {
		Price p = getLatestPrice();
		if(valuesInThisPeriod==valuesPerPeriod){
			if(periods.size()>0)
				calculateCurrentCCI();
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

	private void calculateCurrentCCI() {
		current.TP = (current.close+current.high+current.low)/3;
		if(periods.size()==periodsToCount){
			double cumulativeTP = current.TP;
			for(int i=1;i<periods.size();i++){
				cumulativeTP = cumulativeTP + periods.get(i).TP;
			}
			double tpSma = cumulativeTP/periodsToCount;
			double cumulativeDeviation = abs(current.TP-tpSma);
			for(int i=1;i<periods.size();i++){
				cumulativeDeviation = cumulativeDeviation + abs(periods.get(i).TP-tpSma);
			}
			double meanDeviation = cumulativeDeviation/periodsToCount;
			double CCI = (current.TP  -  tpSma) / (0.015 * meanDeviation);
			double last = getValue();
			setValue(CCI);
			
			/*
			setBuy(true);
			setShort(true);
			
			int risingThreshold = 100;
			int fallingThreshold = 150;
			
			if(CCI>=last){
				if(CCI>=risingThreshold){
					setBuy(false);
				}
				if(CCI<=-risingThreshold){
					setShort(false);
				}
			} else if (includeDescendingInOverBought) {
				if(CCI>=fallingThreshold){
					setBuy(false);
				}
				if(CCI<=-fallingThreshold){
					setShort(false);
				}
			}*/
			int threshold = 150;
			
			if(CCI>=threshold&&(includeDescendingInOverBought||CCI>last)){
				setBuy(false);
			} else {
				setBuy(true);
			}
			if(CCI<=-threshold&&(includeDescendingInOverBought||CCI<last)){
				setShort(false);
			} else {
				setShort(true);
			}
		}
	}



	@Override
	protected void optionUpdated(String name) {

		
	}
	private void updateGraph() {
		updateChart(getInstanceName(), getLatestPrice().getTime(), getValue());
		updateChart(highThreshold, getLatestPrice().getTime(), 100);
		updateChart(lowThreshold, getLatestPrice().getTime(), -100);
	}
	
	private double to2dp(double d){
		d = d * 100;
		d = Math.round(d);
		return d/100;
	}
	@Override
	public String toString() {
		return getIndicatorName();
	}

	public CCIOverIndicator(String name,boolean directional) {
		super();
		setIndicatorName(name);
		includeDescendingInOverBought = !directional;
		setHasOwnChart(true);
		setBuy(false);
		setShort(false);
		setSell(true);
		setCover(true);
		highThreshold = new ChartNameProvider() {
			public String getChartName() {
				return "CCI High threshold";
			}
		};
		lowThreshold = new ChartNameProvider() {
			public String getChartName() {
				return "CCI Low threshold";
			}
		};
	}
}
