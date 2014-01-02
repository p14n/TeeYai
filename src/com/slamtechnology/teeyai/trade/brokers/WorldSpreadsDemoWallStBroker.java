	package com.slamtechnology.teeyai.trade.brokers;

import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;

public class WorldSpreadsDemoWallStBroker extends WorldSpreadsDemoBroker {

	public WorldSpreadsDemoWallStBroker(BrokerageAuthenticator authenticator) {
		super(authenticator);
	}
	public String getName() {
		return "Worldspreads demo Wall St broker";
	}
	@Override
	public String getInstrumentName() {
		return "WALL STREET - Daily Rolling Future";
	}
}
