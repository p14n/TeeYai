package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class EMAPriceIndicator extends EMAIndicator {

	public EMAPriceIndicator() {
		super();
		setIndicatorName("EMAP");
		indicatePrice.setValue(1);
		indicateTrend.setValue(0);
		optionUpdated(null);
	}

	@Override
	public String toString() {
		return super.toString()+" Price";
	}

	@Override
	public Indicator getNewInstance() {
		// TODO Auto-generated method stub
		return new EMAPriceIndicator();
	}

}
