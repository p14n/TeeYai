package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class HighADXInidcator extends ADXIndicator {

	public HighADXInidcator() {
		super("ADXH", 100);
	}

	@Override
	public Indicator getNewInstance() {
		return new HighADXInidcator();
	}

	@Override
	protected void optionUpdated(String name) {
		
	}

}
