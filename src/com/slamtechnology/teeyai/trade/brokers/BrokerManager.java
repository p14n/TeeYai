package com.slamtechnology.teeyai.trade.brokers;

import java.util.HashMap;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.slamtechnology.teeyai.trade.Broker;

public class BrokerManager implements MessageListener {
	
	HashMap<String, Broker> brokers;
	
	public BrokerManager(){
		brokers = new HashMap<String, Broker>();
	}

	public void onMessage(Message msg) {
		try {
			TextMessage txt = (TextMessage)msg;
			String teeYaiName = txt.getText();
			Broker b = brokers.get(teeYaiName);
			if(b!=null){
				b.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerBroker(String teeYaiName,Broker broker){
		brokers.put(teeYaiName, broker);
	}
	public void unregisterBroker(String teeYaiName){
		brokers.remove(teeYaiName);
	}
}
