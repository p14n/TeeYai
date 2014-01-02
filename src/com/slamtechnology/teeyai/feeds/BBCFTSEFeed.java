package com.slamtechnology.teeyai.feeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.slamtechnology.teeyai.feeds.components.BBCFTSE;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.PriceFeed;
import com.slamtechnology.teeyai.prices.PriceThread;

public class BBCFTSEFeed implements PriceFeed {
	
	private Date startTime;
	private Date stopTime;
	private String timeZone;
	FeedComponent[] components;
	
	public static void main(String[] args){
		BBCFTSEFeed feed = new BBCFTSEFeed();
		System.out.println("price: "+feed.getPrice());
		System.out.println("start: "+feed.getStartTime());
		System.out.println("stop: "+feed.getStopTime());
	}
	public BBCFTSEFeed(){
		components = new FeedComponent[1];
		components[0] = new BBCFTSE();
	}

	public void initialise() {/*Nothing to do*/}
	
	public String getDescription() {
		return "BBC FTSE 15 Min delayed";
	}

	public String getName() {
		return "BBCFTSE";
	}

	public double getPrice() {

		double d = 0;
		try {
			WebConversation wc = new WebConversation();
			WebResponse wr = wc.getResponse("http://newsvote.bbc.co.uk/1/shared/fds/hi/business/market_data/ticker/markets/default.stm");
			WebTable[] wts = wr.getTables();
			TableCell tc1 = wts[0].getTableCell(1, 0);
			WebTable wt1 = tc1.getTables()[0];
			TableCell tc2 = wt1.getTableCell(2, 1);
			WebTable wt2 = tc2.getTables()[0];
			d = Double.parseDouble(wt2.getCellAsText(5, 2));
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return d;
	}

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

	public long getIntervalInMillis() {
		// TODO Auto-generated method stub
		return 60000;
	}
	
	public String toString(){
		return getDescription();
	}

	public void getComponentPrices(long now) {
		double price = getPrice();
		Thread t = new Thread(new PriceThread(components[0],price));
		t.start();
	}

	public FeedComponent[] getComponents() {
		// TODO Auto-generated method stub
		return components;
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
		System.out.println("inherit opening times");
		startTime=null;
		stopTime=null;
		for(int i=0;i<components.length;i++){
			System.out.println(components[i].getName());
			System.out.println(components[i].getStartTime());
			System.out.println(components[i].getStopTime());
			if(startTime==null||startTime.getTime()>components[i].getStartTime().getTime()){
				startTime=components[i].getStartTime();
			}
			if(stopTime==null||stopTime.getTime()<components[i].getStopTime().getTime()){
				stopTime=components[i].getStopTime();
			}
		}
		System.out.println("inherit opening times out "+startTime+" "+stopTime);
	}


}
