package com.slamtechnology.teeyai.indicators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.prices.Price;

public class RVIIndicator extends Indicator {
	
	int valueCount = 0;
	IndicatorOption valsToMonitor;
	IndicatorOption signalDistanceFromMidpoint;
	int intervalCount;
	int valuesToMonitor=0;
	double periodHigh=Double.MIN_VALUE;
	double periodLow = Double.MAX_VALUE;
	double distanceFromMidPoint;
	List<Double> highs;
	List<Double> lows;
	RVIVariables highvar;
	RVIVariables lowvar;
	
	public RVIIndicator(){
		super();
		setIndicatorName("RVI");
		setHasOwnChart(true);
		valuesToMonitor=10;
		highs = new Stack();
		lows = new Stack();
		highvar = new RVIVariables();
		lowvar = new RVIVariables();
		distanceFromMidPoint = 10;
		valsToMonitor = new IndicatorOption(IndicatorOption.VALS_TO_MONITOR,"Values to monitor",valuesToMonitor);
		signalDistanceFromMidpoint = new IndicatorOption(IndicatorOption.SIGNAL_DISTANCE_FROM_MIDPOINT,"Signal distance from midpoint",distanceFromMidPoint);
		
		addOption(valsToMonitor);
		addOption(signalDistanceFromMidpoint);
	}

	@Override
	protected void clearInternalValues() {
		valueCount = 0;
		intervalCount=0;
		highs= new Stack();
		lows= new Stack();
		highvar = new RVIVariables();
		lowvar = new RVIVariables();
	}

	@Override
	public Indicator getNewInstance() {
		return new RVIIndicator();
	}

	@Override
	protected void optionUpdated(String name) {
		valuesToMonitor=(int)valsToMonitor.getValue();		
		distanceFromMidPoint = signalDistanceFromMidpoint.getValue();
	}

	@Override
	protected void update() {

		Price p = getLatestPrice();
		double price = p.getPrice();
		
		if(price>periodHigh){
			periodHigh = price;
		}
		if(price<periodLow){
			periodLow=price;
		}

		if(price>0&&intervalCount>=(getInterval()-1)){
			
			if(valueCount<valuesToMonitor){
				valueCount++;
			}
			
			double RVIOrigHigh = calculateRVIOriginal(highvar, periodHigh, valuesToMonitor);
			double RVIOrigLow = calculateRVIOriginal(lowvar, periodLow, valuesToMonitor);
			
			//RVI = (RVIH + RVIL)/2
			double RVI = (RVIOrigHigh+RVIOrigLow)/2;
				
			setValue(RVI);
				
			intervalCount=0;
			periodHigh = Double.MIN_VALUE;
			periodLow = Double.MAX_VALUE;
				
			System.out.print("RVI price "+price+" new RVI "+RVI+" ");
			updateChart(getInstanceName(), p.getTime(), getValue());
			
			System.out.println("");
			
			if(RVI>50+distanceFromMidPoint){
				setBuy(false);
				setSell(true);
				setShort(true);
				setCover(false);
			} else if(RVI>0&&RVI<50-distanceFromMidPoint){
				setBuy(true);
				setSell(false);
				setShort(false);
				setCover(true);
			} else {
				setBuy(false);
				setSell(false);
				setShort(false);
				setCover(false);
			}
			
		} else if(price>0){
			intervalCount++;
		}
	}
	
	private static double standardDeviation(List<Double> prices,double price,int valuesToMonitor){
		
		prices.add(new Double(price));
		if(prices.size()>valuesToMonitor){
			prices.remove(0);
		}
		double priceTotal=0;
		for(int i=0;i<prices.size();i++){
			priceTotal = priceTotal + prices.get(i).doubleValue();
		}
		double mean = (priceTotal/prices.size());
		
		double ongoingSum = 0;
		for(int i=0;i<prices.size();i++){
			ongoingSum = ongoingSum + squareValMinusMean(prices.get(i).doubleValue(), mean);
		}
		ongoingSum = ongoingSum / prices.size();
		ongoingSum = Math.sqrt(ongoingSum);
		return ongoingSum;
		
	}
	private static double squareValMinusMean(double value, double mean){
		
		return (value-mean)*(value-mean);
	}

	public static void main(String[] args){
		Stack<Double> prices = new Stack<Double>();
		double price = 5;
		System.out.println("Adding "+price+" gives "+standardDeviation(prices, price, 4));
		price = 6;
		System.out.println("Adding "+price+" gives "+standardDeviation(prices, price, 4));
		price = 8;
		System.out.println("Adding "+price+" gives "+standardDeviation(prices, price, 4));
		price = 9;
		System.out.println("Adding "+price+" gives "+standardDeviation(prices, price, 4));
	}
	public String toString(){
		return "RVIIndicator";
	}
	
	private static double calculateRVIOriginal(RVIVariables vars,double price,int valuesToMonitor){//,double lastPrice,Stack<Double> prices,int valuesToMonitor){

		double up = 0;
		double down = 0;
		
		if(price>vars.lastValue){
			up = standardDeviation(vars.prices,price,valuesToMonitor);
			down = 0;
		} else {
			up=0;
			down = standardDeviation(vars.prices,price,valuesToMonitor); 
		}
		int valueCount = vars.prices.size();

		System.out.println("up "+up+" down "+down);
		vars.upAvg = (vars.upAvg*(valueCount-1)+up)/valueCount;
		vars.downAvg = (vars.downAvg*(valueCount-1)+down)/valueCount;
		System.out.println("upAvg "+vars.upAvg+" downAvg "+vars.downAvg);
		
		double RVIOrig = 0;
		
		if((vars.upAvg+vars.downAvg)>0){
			
			//NO - 
			// THIS IS THE RVIorig calculation of highs for the period
			//and lows for the period
			RVIOrig = 100*(vars.upAvg/(vars.upAvg+vars.downAvg));
		}

		vars.lastValue=price;
		return RVIOrig;
	}
	
	class RVIVariables{
		double lastValue=0;
		double upAvg=0;
		double downAvg=0;
		List<Double> prices=new ArrayList<Double>();
		
	}
}
