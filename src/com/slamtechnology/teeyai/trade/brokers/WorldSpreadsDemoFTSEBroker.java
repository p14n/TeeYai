package com.slamtechnology.teeyai.trade.brokers;

import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;

public class WorldSpreadsDemoFTSEBroker extends WorldSpreadsDemoBroker {

	public WorldSpreadsDemoFTSEBroker(BrokerageAuthenticator authenticator) {
		super(authenticator);
	}
	public String getName() {
		return "Worldspreads demo FTSE broker";
	}
	@Override
	public String getInstrumentName() {
		return "UK 100 - Daily Rolling Future";
	}
}
