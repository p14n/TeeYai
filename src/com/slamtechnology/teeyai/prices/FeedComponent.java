package com.slamtechnology.teeyai.prices;

import java.util.Date;
import java.util.TimeZone;

public abstract class FeedComponent {
	
	private Date startTime;
	private Date stopTime;
	private TimeZone timeZone;

	public abstract String getName();
	public abstract String getDescription();
	public abstract String getInstrumentName();
	public abstract String getPriceRequestURL();
	public String toString(){
		return getDescription();
	}
	public void setStartTime(Date date){
		this.startTime=date;
	}
	public void setStopTime(Date date){
		this.stopTime=date;
	}
	public TimeZone getTimeZone(){
		return timeZone;
	}
	public abstract void setOpeningTime(long now);
	
	public Date getStartTime() {
		return startTime;
	}
	public Date getStopTime() {
		return stopTime;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}



}
