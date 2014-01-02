package com.slamtechnology.teeyai.ui.test;

import com.slamtechnology.teeyai.FeedMonitor;
import com.slamtechnology.teeyai.name.FeedMonitorName;

public class TestFeedMonitor extends FeedMonitor {

	public TestFeedMonitor() {
		super(new TestChartCreator(new FeedMonitorName("test",1)), new FeedMonitorName("test",1), new TestFeedComponent(), 1, true);
	}

}
