package com.slamtechnology.teeyai.feeds.components;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import com.slamtechnology.teeyai.prices.FeedComponent;

public class WorldSpreadsDemoWallSt extends FeedComponent {

	@Override
	public String getName() {
		return "WSDemoWall";
	}

	@Override
	public String getDescription() {
		return "Worldspreads demo Wall St";
	}

	@Override
	public String getInstrumentName() {
		return "WALL STREET - Daily Rolling Future";
	}

	@Override
	public String getPriceRequestURL() {
		return null;
	}

	@Override
	public void setOpeningTime(long now) {
		DateTime nowDateTime = new DateTime(now);
		if(nowDateTime.getDayOfWeek()==DateTimeConstants.SATURDAY){
			nowDateTime = nowDateTime.plusDays(2);
		} else if(nowDateTime.getDayOfWeek()==DateTimeConstants.SUNDAY){
			nowDateTime = nowDateTime.plusDays(1);
		}
		nowDateTime = nowDateTime.withMillisOfDay(0);
		setStartTime(nowDateTime.withHourOfDay(14).withMinuteOfHour(30).toDate());
		setStopTime(nowDateTime.withHourOfDay(21).withMinuteOfHour(00).toDate());
		if(getStopTime().getTime()<now){
			setOpeningTime(nowDateTime.plusDays(1).getMillis());
		}
	}
}
