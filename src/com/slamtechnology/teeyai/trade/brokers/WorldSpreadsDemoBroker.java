package com.slamtechnology.teeyai.trade.brokers;

import java.util.logging.Logger;

import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;
import com.slamtechnology.teeyai.trade.Quote;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsAdvancedApp;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsApp;

public abstract class WorldSpreadsDemoBroker extends AbstractBroker {

	public long getID() {
		return 3;
	}
	
	private WorldSpreadsApp getLoggedInApp(){
		WorldSpreadsApp app = WorldSpreadsApp.get();
		if(!app.isLoggedIn()){
			getAuthenticator().getSession();
		}
		return app;
	}
	
	@Override
	protected Quote getMarketPrice() throws Exception {
		return getLoggedInApp().getQuote(getInstrumentName(),true);
	}

	public WorldSpreadsDemoBroker(BrokerageAuthenticator authenticator) {
		super(authenticator);
	}

	public abstract String getInstrumentName();

	@Override
	protected Quote confirm(Quote q,boolean buy)
			throws Exception {
		
		Logger log = getLogger();

		if(buy){
			log.info("Looking to buy at "+q.getBid());
		} else {
			log.info("Looking to sell at "+q.getAsk());
		}

		WorldSpreadsApp app = getLoggedInApp(); 
		boolean success = app.trade(getInstrumentName(),myPosition().getAmount(), buy);
		if(myPosition().isOpen()){
			if(success){
				q.setTradeIsOpened(true);
			}
		} else {
			if(success){
				q.setTradeIsClosed(true);
			}
		}
		

		return q;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Quote closeTrade(double amount,boolean isLong) {
		return WorldSpreadsApp.get().closeTrade(amount,isLong);
	}

}
