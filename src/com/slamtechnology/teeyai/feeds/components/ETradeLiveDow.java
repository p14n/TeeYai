package com.slamtechnology.teeyai.feeds.components;

import java.util.Calendar;
import java.util.TimeZone;

import com.slamtechnology.teeyai.prices.FeedComponent;

public class ETradeLiveDow extends FeedComponent {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "ETrade Dow Live";
	}

	@Override
	public String getInstrumentName() {
		// TODO Auto-generated method stub
		return "Wall+Street+Rolling+Daily";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ETradeDowLive";
	}
	@Override
	public void setOpeningTime(long now) {
		setTimeZone(TimeZone.getTimeZone("GMT-4"));
		Calendar cal = Calendar.getInstance(getTimeZone());
		cal.setTimeInMillis(now);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			cal.add(Calendar.DATE, 2);
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			cal.add(Calendar.DATE, 1);
		}		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		setStartTime(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 30);
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
		return "http://www.etradespreadbetting.com/common/frontend/member/httpdata/getmarketprice.aspx?m=?&z="+System.currentTimeMillis();
	}

}
