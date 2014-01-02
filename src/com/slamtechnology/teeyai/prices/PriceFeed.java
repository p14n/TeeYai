package com.slamtechnology.teeyai.prices;

import java.util.Date;

public interface PriceFeed {

	public void getComponentPrices(long now);
	public FeedComponent[] getComponents();
	public long getIntervalInMillis();
	public String getName();
	public String getDescription();
	public Date getStartTime();
	public Date getStopTime();
	public void setFeedTimes(long now);
	public void initialise();


}
