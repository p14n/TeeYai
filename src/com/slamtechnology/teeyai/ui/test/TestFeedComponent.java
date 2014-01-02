package com.slamtechnology.teeyai.ui.test;

import com.slamtechnology.teeyai.prices.FeedComponent;

public class TestFeedComponent extends FeedComponent {

	@Override
	public String getName() {
		return "test";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getInstrumentName() {
		return null;
	}

	@Override
	public String getPriceRequestURL() {
		return null;
	}

	@Override
	public void setOpeningTime(long now) {

	}

}
