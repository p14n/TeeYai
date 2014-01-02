package com.slamtechnology.teeyai.prices;

import java.sql.Timestamp;


public class Price {
	
	public Price() {
		super();
	}
	public Price(double price) {
		super();
		this.price = price;
	}
	long priceID;
	String name;
	double price;
	double volume;
	long time;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public long getPriceID() {
		return priceID;
	}
	public void setPriceID(long priceID) {
		this.priceID = priceID;
	}
	public Timestamp getTimestamp(){
		return new Timestamp(time);
	}
	public void setTimestamp(Timestamp t){
		if(t!=null){
			time = t.getTime();
		}
	}

}
