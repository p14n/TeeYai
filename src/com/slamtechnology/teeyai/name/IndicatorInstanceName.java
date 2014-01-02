package com.slamtechnology.teeyai.name;

import com.slamtechnology.teeyai.Indicator;

public class IndicatorInstanceName implements ChartNameProvider {

	public String getName() {
		return name;
	}


	FeedMonitorName feedMonitorName;
	String name;
	
	public IndicatorInstanceName(FeedMonitorName feedMonitorName,Indicator i) {
		super();
		this.feedMonitorName = feedMonitorName;
		name = feedMonitorName.getName()+":"+i.getIndicatorName();
	}

	
	@Override
	public String toString() {
		return "IndicatorInstanceName [" + name + "]";
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
		IndicatorInstanceName other = (IndicatorInstanceName) obj;
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
