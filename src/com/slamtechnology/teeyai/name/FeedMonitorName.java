package com.slamtechnology.teeyai.name;

import com.slamtechnology.teeyai.prices.FeedComponent;

public class FeedMonitorName implements ChartNameProvider {
	
	public int getInterval() {
		return interval;
	}
	public String getFeedName() {
		return feedName;
	}
	String feedName;
	int interval;
	String name;
	public FeedMonitorName(FeedComponent feed, int interval) {
		this(feed.getName(),interval);
	}
	public FeedMonitorName(String feedName, int interval) {
		super();
		this.feedName = feedName;
		this.interval = interval;
		name = feedName+":"+interval;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedMonitorName other = (FeedMonitorName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public String getChartName() {
		return name;
	}

}
