package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class MACDLineIndicator extends MACDIndicator {
	
	public MACDLineIndicator() {
		setIndicatorName("MACDX");
		setHasOwnChart(true);
		setup();
	}

	@Override
	public String toString() {
		return "MACD Crossover";
	}

	@Override
	public Indicator getNewInstance() {
		return new MACDLineIndicator();
	}

	@Override
	protected void update() {
		super.update();
		setBuy(!wasOverTheLine);
		setCover(!wasOverTheLine);
		setSell(wasOverTheLine);
		setShort(wasOverTheLine);
	}

}
