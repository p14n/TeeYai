package com.slamtechnology.teeyai.prices;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.slamtechnology.teeyai.ServiceManager;

public class PriceListener implements MessageListener {

	String feedname;
	public PriceListener(String feedname){
		this.feedname = feedname;
	}
	
	public void onMessage(Message message) {
		try {
			TextMessage text = (TextMessage) message;
			Price p = new Price();
			p.setName(feedname);
			p.setTime(System.currentTimeMillis());
			p.setPrice(Double.parseDouble(text.getText()));
			ServiceManager.getInstance().updatePrice(feedname, p);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
