package com.slamtechnology.teeyai.feeds;

import java.util.Date;

import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.PriceFeed;
import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;

public abstract class ETradeFeed implements PriceFeed {

	private Date startTime;
	private Date stopTime;
	private String timeZone;
	protected BrokerageAuthenticator auth;


	public abstract String getDescription();
	public void initialise() {/*Nothing to do*/}

	public long getIntervalInMillis() {
		// TODO Auto-generated method stub
		return 10000;
	}

	public abstract String getName();

	//protected abstract String getPriceRequestURL();
	
	public Date getStartTime() {
		// TODO Auto-generated method stub
		return startTime;
	}

	public Date getStopTime() {
		// TODO Auto-generated method stub
		return stopTime;
	}

	public String getTimeZone() {
		// TODO Auto-generated method stub
		return timeZone;
	}

	public void setStartTime(Date date) {
		startTime = date;

	}

	public void setStopTime(Date date) {
		stopTime = date;

	}
	public String toString(){
		return getDescription();
	}

	public void setFeedTimes(long now) {
		FeedComponent[] components = getComponents();
		for(int i=0;i<components.length;i++){
			components[i].setOpeningTime(now);
		}
		inheritOpeningTimes();
	}
	
	private void inheritOpeningTimes(){
		FeedComponent[] components = getComponents();
		startTime=null;
		stopTime=null;
		for(int i=0;i<components.length;i++){
			if(startTime==null||startTime.getTime()>components[i].getStartTime().getTime()){
				startTime=components[i].getStartTime();
			}
			if(stopTime==null||stopTime.getTime()<components[i].getStopTime().getTime()){
				stopTime=components[i].getStopTime();
			}
		}
	}
	


}
