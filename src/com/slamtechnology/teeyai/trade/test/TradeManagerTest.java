package com.slamtechnology.teeyai.trade.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.TeeYai;
import com.slamtechnology.teeyai.feeds.components.ETradeDemoFTSE;
import com.slamtechnology.teeyai.indicators.EMAIndicator;
import com.slamtechnology.teeyai.indicators.test.BinaryIndicator;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.teeyai.trade.Position;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.TradeStrategy;

public class TradeManagerTest extends TestCase {
	
	public static void main(String args[]){

		java.text.SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy hh:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			System.out.println(sdf.parse("28092007 00:00").getTime());
			System.out.println(sdf.parse("26092007 00:00").getTime());
			System.out.println((new Date(1190996097549L)));
			System.out.println((new Date(1190106000000L)));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testRange(){
		
		System.out.println("0");
		TradeManager tm = new TradeManager();
		
		TeeYai ty = new TeeYai("dean",tm); 

		FeedComponent feed = new ETradeDemoFTSE(); 
		//Indicator macslow = new MACDIndicator();
		Indicator emaslow = new EMAIndicator();
		Indicator emamid = new EMAIndicator();
		Indicator emafast = new EMAIndicator();
		//Indicator macfast = new MACDIndicator();
		//Indicator rvi = new RVIIndicator();
		int intervalslow = 30;
		int intervalMid = 6;
		int intervalfast = 1;
		FeedMonitorName feedMonitorNameSlow = new FeedMonitorName(feed, intervalslow);
		FeedMonitorName feedMonitorNameMid = new FeedMonitorName(feed, intervalMid);
		FeedMonitorName feedMonitorNameFast = new FeedMonitorName(feed, intervalfast);
		
		System.out.println("1");
		ty.addFeed(feed, intervalslow,null);
		ty.addFeed(feed, intervalMid,null);
		ty.addFeed(feed, intervalfast,null);
		ty.addIndicator(feedMonitorNameSlow,emaslow );
		ty.addIndicator(feedMonitorNameMid,emamid );
		ty.addIndicator(feedMonitorNameFast,emafast );
		//ty.addIndicator(feedMonitorNameMid,macfast );

		//ty.addIndicator(feedMonitorNameFast,rvi );
		//ty.addIndicator(feedMonitorNameFast,macfast );
		
		//emaslow.setOption(IndicatorOption.VALS_TO_MONITOR, 22);
		emaslow.setOption(IndicatorOption.VALS_TO_MONITOR, 15);
		
		TradeStrategy buy = new TradeStrategy("buy",TradeStrategy.BUY);
		buy.addIndicator(emaslow.getInstanceName());
		//buy.addIndicator(emafast.getInstanceName());
		//buy.addIndicator(emamid.getInstanceName());

		TradeStrategy sell = new TradeStrategy("sell",TradeStrategy.SELL);
		sell.addIndicator(emaslow.getInstanceName());
		
		TradeStrategy cover = new TradeStrategy("cover",TradeStrategy.COVER);
		cover.addIndicator(emaslow.getInstanceName());

		TradeStrategy djort = new TradeStrategy("short",TradeStrategy.SHORT);
		djort.addIndicator(emaslow.getInstanceName());
		//djort.addIndicator(emafast.getInstanceName());
		//djort.addIndicator(emamid.getInstanceName());


		tm.addStrategy(buy);
		tm.addStrategy(sell);
		tm.addStrategy(cover);
		tm.addStrategy(djort);
		
		tm.setTrading(true);
		tm.setSpread(0.5);
		tm.setIgnoreFirstInstruction(true);
		//tm.setStopSpread(15);
		tm.setTargetProfit(10);
		//tm.setStopSpread(5);
		//tm.setProfitTake(10);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		long start=0;
		long end=0;
		try {
			cal.setTime(sdf.parse("28/09/2007 08:00"));
			start=cal.getTimeInMillis();
			cal.setTime(sdf.parse("28/09/2007 10:00"));
			end=cal.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ty.backTest(feed.getName(),start,end);

		
	}
	
	public void testUpdate(){
		
		String feedName = "test";
		
		FeedMonitorName fm = new FeedMonitorName(feedName, 1);
		
		Indicator ind = new BinaryIndicator();
		ind.setFeedName(fm);
		Indicator ind2 = new BuyOnlyIndicator();
		ind.setFeedName(fm);
		
		
		TradeStrategy buy = new TradeStrategy("buy",TradeStrategy.BUY);
		buy.addIndicator(ind.getInstanceName());
		buy.addIndicator(ind2.getInstanceName());
		TradeStrategy sell = new TradeStrategy("sell",TradeStrategy.SELL);
		sell.addIndicator(ind.getInstanceName());
		sell.addIndicator(ind2.getInstanceName());
		TradeStrategy cover = new TradeStrategy("cover",TradeStrategy.COVER);
		cover.addIndicator(ind.getInstanceName());
		cover.addIndicator(ind2.getInstanceName());
		TradeStrategy djort = new TradeStrategy("djort",TradeStrategy.SHORT);
		djort.addIndicator(ind.getInstanceName());
		djort.addIndicator(ind2.getInstanceName());
		
		TradeManager tm = new TradeManager();
		tm.addIndicator("test", ind);
		tm.addIndicator("test", ind2);
		
		tm.addStrategy(buy);
		tm.addStrategy(sell);
		tm.addStrategy(cover);
		tm.addStrategy(djort);
		
		tm.setTrading(true);
		tm.setSpread(1);
		
		Price p = new Price();
		p.setPrice(8);

		ind.update(p);
		ind2.update(p);
		tm.update("test",p);
		
		/*System.out.println("b "+ind.isBuy());
		System.out.println("s "+ind.isSell());
		System.out.println("c "+ind.isCover());
		System.out.println("sh "+ind.isShort());*/
		
		System.out.println("open   \n"+getPositionText(tm.getOpenPositions()));
		System.out.println("closed \n"+getPositionText(tm.getRecentPositions()));

		p.setPrice(12);
		ind.update(p);
		ind2.update(p);
		tm.update("test",p);
		
		System.out.println("open   \n"+getPositionText(tm.getOpenPositions()));
		System.out.println("closed \n"+getPositionText(tm.getRecentPositions()));
		
		p.setPrice(9);
		ind.update(p);
		ind2.update(p);
		tm.update("test",p);
		
		System.out.println("open   \n"+getPositionText(tm.getOpenPositions()));
		System.out.println("closed \n"+getPositionText(tm.getRecentPositions()));

		p.setPrice(11);
		ind.update(p);
		ind2.update(p);
		tm.update("test",p);
		
		System.out.println("open   \n"+getPositionText(tm.getOpenPositions()));
		System.out.println("closed \n"+getPositionText(tm.getRecentPositions()));

	}
	private String getPositionText(ArrayList<Position> positions){
		StringBuffer sb = new StringBuffer("");
		if(positions!=null){
			for(Position p : positions){
				sb.append((p.isLong()?"Buy":"Sell")+" of "+p.getFeedName()+" at "+p.getEntryPoint()+", profit "+p.getProfit()+"\n");
			}
		}
		return sb.toString();
	}
	
	class BuyOnlyIndicator extends Indicator {
		
		public BuyOnlyIndicator(){
			setIndicatorName("BO");
			setBuy(true);
			setCover(false);
			setSell(true);
			setShort(false);
		}

		@Override
		public Indicator getNewInstance() {
			// TODO Auto-generated method stub
			return new BuyOnlyIndicator();
		}

		@Override
		protected void optionUpdated(String name) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void update() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void clearInternalValues() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
