package com.slamtechnology.teeyai.prices;

import com.slamtechnology.teeyai.ServiceManager;

public class PriceThread implements Runnable {
	
	FeedComponent feed;
	double price;
	
	public PriceThread(FeedComponent feed,double price){
		this.feed=feed;
		this.price = price;
	}

	public void run() {

		System.out.println("");
		System.out.println(feed.getName()+" price "+price);
		if(price>0){
			try{
				PriceMessenger.sendPriceMessage(feed.getName(), price);
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				PriceDAO.savePrice(feed.getName(), price);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}

}
