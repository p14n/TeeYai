package com.slamtechnology.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsApp;

public class LogSetup {

	private static SimpleDateFormat sdf;
	public static Logger backLog;
	public static Logger tradesLog;
	public static Logger indicatorLog;
	
	static{
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		tradesLog = setupLog(TradeManager.class, "trades.log");
		indicatorLog = setupLog(Indicator.class, "indicator.log");
		backLog = setupLog(TradeManager.class.getName()+"backtest","backtest.log");

	}
	
	private static Logger setupLog(Class c,String file){
		return setupLog(c.getName(),file);
	}
	private static Logger setupLog(String name,String file){
		try {
			FileHandler handler = new FileHandler(file);
			handler.setFormatter(new SimpleFormatter());
			Logger logger = Logger.getLogger(name);
	        logger.addHandler(handler);
	        return logger;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static {
		createLog(Broker.class.getName(), "broker.log");
		createLog("org.apache.activemq", "activemq.log");
	}
	
	private static void createLog(String name,String file){
		try {
			Handler fh = new FileHandler(file);
			fh.setFormatter(new SimpleFormatter());
			fh.setLevel(Level.ALL);
		    Logger.getLogger(name).addHandler(fh);
		    Logger.getLogger(name).setLevel(Level.ALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
