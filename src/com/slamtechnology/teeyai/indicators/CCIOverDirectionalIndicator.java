package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class CCIOverDirectionalIndicator extends CCIOverIndicator {

	public CCIOverDirectionalIndicator() {
		super("CCIOD", true);
	}

	@Override
	public Indicator getNewInstance() {
		return new CCIOverDirectionalIndicator();
	}

}
