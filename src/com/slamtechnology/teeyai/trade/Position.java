package com.slamtechnology.teeyai.trade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Position {
	
	private static SimpleDateFormat msgDateFmt = new SimpleDateFormat("yyyyMMdd.HHmmss");
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(entryPoint);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (longTrade ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (Double.doubleToLongBits(amount) != Double
				.doubleToLongBits(other.amount))
			return false;
		if (Double.doubleToLongBits(entryPoint) != Double
				.doubleToLongBits(other.entryPoint))
			return false;
		if (longTrade != other.longTrade)
			return false;
		return true;
	}
	boolean longTrade;
	boolean open;
	protected boolean isSuccess() {
		return success;
	}
	public boolean hasClosingStrategy(){
		return closingStrategy!=null;
	}

	protected void setSuccess(boolean success) {
		this.success = success;
	}
	double entryPoint;
	double tolerance;
	boolean success = false;
	protected double getTolerance() {
		return tolerance;
	}

	protected void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	double profit;
	double amount;
	double exitPoint;
	double highPoint;
	double lowPoint;
	double stop;
	String feedName;
	String openingStrategy;
	String closingStrategy;
	Date opened;
	Date closed;
	
	private Position(){
		
	}
	
	public Date getOpened() {
		return opened;
	}

	public void setOpened(Date opened) {
		this.opened = opened;
	}

	public Date getClosed() {
		return closed;
	}

	public void setClosed(Date closed) {
		this.closed = closed;
	}

	public Position(String feedname,double entry,boolean longTrade){
		this.feedName = feedname;
		this.entryPoint = entry;
		this.longTrade = longTrade;
	}
	
	public boolean isLong() {
		return longTrade;
	}
	public void setLong(boolean longTrade) {
		this.longTrade = longTrade;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public double getEntryPoint() {
		return entryPoint;
	}
	public void setEntryPoint(double entryPoint) {
		this.entryPoint = entryPoint;
	}
	public double getExitPoint() {
		return exitPoint;
	}
	public void setExitPoint(double exitPoint) {
		this.exitPoint = exitPoint;
	}
	public String getFeedName() {
		return feedName;
	}
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}

	public double getHighPoint() {
		return highPoint;
	}

	public void setHighPoint(double highPoint) {
		this.highPoint = highPoint;
	}

	public double getLowPoint() {
		return lowPoint;
	}

	public void setLowPoint(double lowPoint) {
		this.lowPoint = lowPoint;
	}

	public String getOpeningStrategy() {
		return openingStrategy;
	}

	public void setOpeningStrategy(String openingStrategy) {
		this.openingStrategy = openingStrategy;
	}

	public String getClosingStrategy() {
		return closingStrategy;
	}

	public void setClosingStrategy(String closingStrategy) {
		this.closingStrategy = closingStrategy;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}
	public void setProfit() {
		this.profit = longTrade?(exitPoint-entryPoint):(entryPoint-exitPoint);
		this.profit = Math.round(this.profit*10.0)/10.0;
	}

	public double getStop() {
		return stop;
	}

	public void setStop(double stop) {
		this.stop = stop;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	private Map<String,String> values = new HashMap<String, String>();
	
	
	public String value(String val){
		return values.get(val);
	}
	public String value(String key,String val){
		return values.put(key,val);
	}
	
	public static Position fromMessageString(String input){
		Position p = new Position();
		p.populate(input);
		p.setEntryPoint(Double.parseDouble(p.value("entryPoint")));
		p.setExitPoint(Double.parseDouble(p.value("exitPoint")));
		p.setLong(Boolean.parseBoolean(p.value("longTrade")));
		p.setOpen(Boolean.parseBoolean(p.value("open")));
		p.setSuccess(Boolean.parseBoolean(p.value("success")));
		p.setFeedName(p.value("feedName"));
		p.setTolerance(Double.parseDouble(p.value("tolerance")));
		p.setAmount(Double.parseDouble(p.value("amount")));
		p.setOpened(dateValue(p.values,"opened"));
		p.setClosed(dateValue(p.values,"closed"));
		
		p.values.remove("amount");
		p.values.remove("entryPoint");
		p.values.remove("exitPoint");
		p.values.remove("longTrade");
		p.values.remove("feedName");
		p.values.remove("open");
		p.values.remove("tolerance");
		p.values.remove("success");
		p.values.remove("opened");
		p.values.remove("closed");
		return p;
	}
	private static Date dateValue(Map<String,String> vals,String key){
		String val = vals.get(key);
		if(val!=null)
			try {
				return msgDateFmt.parse(val);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		return null;
	}
	public String toMessageString(){
		StringBuffer s = new StringBuffer();
		for(Entry<String, String> entry:values.entrySet()){
			s.append(entry.getKey()+":"+entry.getValue()+",");
		}
		s.append("amount:"+amount+",");
		s.append("entryPoint:"+entryPoint+",");
		s.append("exitPoint:"+exitPoint+",");
		s.append("longTrade:"+longTrade+",");
		s.append("feedName:"+feedName+",");
		s.append("open:"+open+",");
		s.append("tolerance:"+tolerance+",");
		s.append("success:"+success);
		if(opened!=null)s.append(",opened:"+msgDateFmt.format(opened));
		if(closed!=null)s.append(",closed:"+msgDateFmt.format(closed));
		return s.toString();
	}
	private void populate(String allOptions){
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


}
