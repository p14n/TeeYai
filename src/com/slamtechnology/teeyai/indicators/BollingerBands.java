package com.slamtechnology.teeyai.indicators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;
import com.slamtechnology.teeyai.prices.Price;

public class BollingerBands extends Indicator {
	
	List<Price> prices;
	int valuesToMonitor;
	IndicatorOption valuesOption;
	
	public BollingerBands(){
		setIndicatorName("Boll");
		valuesToMonitor=20;
		valuesOption = new IndicatorOption(IndicatorOption.VALS_TO_MONITOR,"Values to monitor",valuesToMonitor);
		addOption(valuesOption);
		prices = new ArrayList<Price>();
	}
	public String toString(){
		return "Bollinger bands";
	}

	@Override
	protected void clearInternalValues() {
		prices = new Stack<Price>();
	}

	@Override
	public Indicator getNewInstance() {
		return new BollingerBands();
	}

	@Override
	protected void optionUpdated(String name) {
		valuesToMonitor = (int)valuesOption.getValue();
	}

	@Override
	protected void update() {
		Price price = getLatestPrice();
		if(prices.size()>=valuesToMonitor){
			prices.remove(0);
		}
		prices.add(price);
		double MA = 0;
		for(Price p:prices){
			//System.out.print(p.getPrice()+" + ");
			MA = MA+p.getPrice();
		}
		//System.out.print(" = "+MA+" number "+prices.size());//" bandwidth "+bandwidth);
		MA = MA/prices.size();
		//System.out.println(" MA "+MA);//" bandwidth "+bandwidth);
		double bandwidth = calculateBandWidth(MA, prices, 2);
		setValue(bandwidth);
		
		System.out.println("Price "+price.getPrice()+" bandwidth "+bandwidth);
		//System.out.println("Prices "+prices.size()+" "+MA);

			updateChart(new BollingerNameProvider(getInstanceName(),"H"), price.getTime(), MA+bandwidth);
			updateChart(new BollingerNameProvider(getInstanceName(),"M"), price.getTime(), MA);
			updateChart(new BollingerNameProvider(getInstanceName(),"L"), price.getTime(), MA-bandwidth);
	}
	private static double calculateBandWidth(double MA,List<Price> prices,int deviations){
		
		double a=0;
		for(Price p:prices){
			double b = p.getPrice()-MA;
			a = a+(b*b);
		}
		a = a/prices.size();
		double deviation = Math.sqrt(a);
		//System.out.println("Deviation "+deviation);
		return deviations*deviation;
	}
	
	private class BollingerNameProvider implements ChartNameProvider {
		
		private String bolName;
		private IndicatorInstanceName instanceName;

		public BollingerNameProvider(
				IndicatorInstanceName instanceName,String bolName) {
			super();
			this.bolName = bolName;
			this.instanceName = instanceName;
		}

		public String getChartName() {
			return instanceName+bolName;
		}
		
	}

}
