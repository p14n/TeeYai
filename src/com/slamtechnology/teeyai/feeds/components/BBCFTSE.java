package com.slamtechnology.teeyai.feeds.components;

import java.util.Calendar;
import java.util.TimeZone;

import com.slamtechnology.teeyai.prices.FeedComponent;

public class BBCFTSE extends FeedComponent {

	@Override
	public String getInstrumentName() {
		// TODO Auto-generated method stub
		return "FTSE";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "BBC FTSE 15 Min delayed";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "BBCFTSE";
	}
	@Override
	public void setOpeningTime(long now) {
		Calendar cal = Calendar.getInstance();
		setTimeZone(cal.getTimeZone());
		cal.setTimeInMillis(now);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			cal.add(Calendar.DATE, 2);
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			cal.add(Calendar.DATE, 1);
		}		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		setStartTime(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 45);
		setStopTime(cal.getTime());
		if(cal.getTimeInMillis()<now){
			advanceOpeningTimes();
		}
	}

	private void advanceOpeningTimes() {
			
			System.out.println("");
			System.out.println("Changing component start time");
			Calendar cal = Calendar.getInstance(getTimeZone());
			cal.setTime(getStartTime());
			cal.add(Calendar.DATE, 1);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				cal.add(Calendar.DATE, 2);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				cal.add(Calendar.DATE, 1);
			}
			setStartTime(cal.getTime());
			cal.setTime(getStopTime());
			cal.add(Calendar.DATE, 1);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				cal.add(Calendar.DATE, 2);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				cal.add(Calendar.DATE, 1);
			}
			setStopTime(cal.getTime());
			System.out.println(getName()+" new start time "+getStartTime());
			System.out.println(getName()+" new stop time  "+getStopTime());
			
	}

	@Override
	public String getPriceRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

}
