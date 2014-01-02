package com.slamtechnology.teeyai.ui;

import java.awt.Component;
import java.util.Date;

import javax.swing.event.InternalFrameAdapter;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.TeeYaiListener;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.name.IndicatorInstanceName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;


public interface TeeYaiDisplay extends TeeYaiListener {

	void remove(Component c);

	void addIndicator(FeedMonitorName feedMonitorName, Indicator selectedItem);

	void removeFeed(FeedMonitorName feedMonitorName);

	void removeIndicator(Indicator myIndicator,IndicatorDisplay indicatorDisplay);

	void updateIndicatorOption(FeedMonitorName feedName,
			IndicatorInstanceName instanceName, String name, double value);

	void addFeed(FeedComponent pf, int interval, FeedMonitorName selectedItem);

	void openTradeManagerDisplay();

	void setTradeManagerEnabled(boolean b);

	void resetIndicators();

	void setBackTesting(boolean selected);

	void backTest(Date from, Date to);

	void openFeedsForEnabledStrategies();

}
