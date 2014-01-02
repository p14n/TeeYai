package com.slamtechnology.teeyai.ui;

import java.util.Map;
import java.util.Set;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.TeeYai;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.trade.Broker;

public class TeeYaiApp {

	public static void main (String args[]){
		ServiceManager s = ServiceManager.getInstance();
		try {
			s.createDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			s.createPriceMessageService();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FeedComponent[] feeds = s.getFeedComponents();
		Broker[] brokers = s.getBrokers();
		TeeYai ty = s.getTeeYai("dean");
		Indicator[] indicators = s.getIndicators();
		Map<Integer, Map<Integer,Set<Integer>>> datesToInclude = s.getAllAvailableBackTestDates();
		TeeYaiDesktopDisplay display = new TeeYaiDesktopDisplay(ty, feeds, brokers,indicators,datesToInclude);
		display.setVisible(true);
	}
}
