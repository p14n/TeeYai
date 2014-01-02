package com.slamtechnology.teeyai.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Quote {
	
	private double bid;
	private double ask;
	private Map<String,String> values = new HashMap<String, String>();
	
	
	
	public String value(String val){
		return values.get(val);
	}
	public String value(String key,String val){
		return values.put(key,val);
	}
	public double getBid() {
		return bid;
	}
	public void setBid(double bid) {
		this.bid = bid;
	}
	public double getAsk() {
		return ask;
	}
	public void setAsk(double ask) {
		this.ask = ask;
	}
	
	public void populate(String allOptions){
		StringTokenizer st = new StringTokenizer(allOptions,",");
		while(st.hasMoreTokens()){
			String pair = st.nextToken();
			if(pair.indexOf(":")>-1){
				StringTokenizer st2 = new StringTokenizer(pair,":");
				String k = st2.nextToken();
				if(st2.hasMoreTokens()){
					String v = st2.nextToken();
					value(k, v);
				}
			}
		}
	}
	public boolean tradeIsOpened(){
		return "Opened".equalsIgnoreCase(value("TradeStatus"));
	}
	public boolean tradeIsClosed(){
		return "Closed".equalsIgnoreCase(value("TradeStatus"));
	}
	public void setTradeIsOpened(boolean opened){
		value("TradeStatus", opened?"Opened":"");
	}
	public void setTradeIsClosed(boolean closed){
		value("TradeStatus", closed?"Closed":"");
	}

}
