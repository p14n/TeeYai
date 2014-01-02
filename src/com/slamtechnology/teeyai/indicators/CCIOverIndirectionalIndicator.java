package com.slamtechnology.teeyai.indicators;

import com.slamtechnology.teeyai.Indicator;

public class CCIOverIndirectionalIndicator extends CCIOverIndicator {

	public CCIOverIndirectionalIndicator() {
		super("CCIOI", false);
	}

	@Override
	public Indicator getNewInstance() {
		return new CCIOverIndirectionalIndicator();
	}

}
