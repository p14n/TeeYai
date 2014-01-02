package com.slamtechnology.teeyai.feeds;

import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.feeds.components.WorldSpreadsDemoFTSE;
import com.slamtechnology.teeyai.feeds.components.WorldSpreadsDemoWallSt;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.PriceThread;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.BrokerageMessageExecutor;
import com.slamtechnology.teeyai.trade.Quote;
import com.slamtechnology.teeyai.trade.brokers.SessionAuthenicationException;
import com.slamtechnology.teeyai.trade.brokers.WorldSpreadsDemoAuthenticator;
import com.slamtechnology.teeyai.trade.brokers.WorldSpreadsDemoFTSEBroker;
import com.slamtechnology.teeyai.trade.brokers.WorldSpreadsDemoWallStBroker;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsAdvancedApp;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsApp;

public class WorldSpreadsDemoFeed extends WorldSpreadsFeed {
	
	public void initialise() {
		auth = new WorldSpreadsDemoAuthenticator();
		Broker b = new WorldSpreadsDemoFTSEBroker(auth);
		BrokerageMessageExecutor m  = new BrokerageMessageExecutor(b);
		ServiceManager.getInstance().addListener(b.getName(), m);
		b = new WorldSpreadsDemoWallStBroker(auth);
		m  = new BrokerageMessageExecutor(b);
		ServiceManager.getInstance().addListener(b.getName(), m);
	}

	FeedComponent[] components = new FeedComponent[]{
			new WorldSpreadsDemoFTSE(),
			new WorldSpreadsDemoWallSt()};
	
	public static void main(String args[]){
		WorldSpreadsDemoFeed d = new WorldSpreadsDemoFeed();
		d.initialise();
		d.setFeedTimes(System.currentTimeMillis());
		d.getComponentPrices(System.currentTimeMillis());
	}
	
	public void getComponentPrices(long now) {
		
		if(!WorldSpreadsApp.get().isLoggedIn()){
			auth.getSession();
		}

		if(!auth.isLoggingOn()){

			FeedComponent[] components = getComponents();
			
			try {
				
				for(int i=0;i<components.length;i++){
					
					if(now>components[i].getStartTime().getTime()&&now<components[i].getStopTime().getTime()){

						Quote q = WorldSpreadsAdvancedApp.get().getQuote(components[i].getInstrumentName());
						if(q!=null){
							double price = (q.getBid()+q.getAsk())/2;
							Thread t = new Thread(new PriceThread( components[i],price));
							t.start();
						}
						
					}
				}
				
			} catch (SessionAuthenicationException e){
				e.printStackTrace();
				auth.destroySession();
			}
		}
	}


	public FeedComponent[] getComponents() {
		return components;
	}

	@Override
	public String getDescription() {
		return "World spreads demo";
	}

	@Override
	public String getName() {
		return "WorldSpreadsDemo";
	}

}
