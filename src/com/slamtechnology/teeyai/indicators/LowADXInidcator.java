package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class LowADXInidcator extends ADXIndicator {

	public LowADXInidcator() {
		super("ADXL", 66.66);
	}

	@Override
	public Indicator getNewInstance() {
		return new LowADXInidcator();
	}

	@Override
	protected void optionUpdated(String name) {
		
	}

}
