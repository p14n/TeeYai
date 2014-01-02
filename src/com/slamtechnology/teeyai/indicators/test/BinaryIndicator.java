package com.slamtechnology.teeyai.indicators.test;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.prices.Price;

public class BinaryIndicator extends Indicator {
	
	public BinaryIndicator(){
		setIndicatorName("Bin");

	}

	@Override
	public Indicator getNewInstance() {
		// TODO Auto-generated method stub
		return new BinaryIndicator();
	}

	@Override
	protected void optionUpdated(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void update() {
		
		Price p = getLatestPrice();
		if(p.getPrice()>10){
			setSell(true);
			setShort(true);
			setBuy(false);
			setCover(false);
		} else {
			setBuy(true);
			setCover(true);
			setSell(false);
			setShort(false);
		}

	}

	@Override
	protected void clearInternalValues() {
		// TODO Auto-generated method stub
		
	}
}
