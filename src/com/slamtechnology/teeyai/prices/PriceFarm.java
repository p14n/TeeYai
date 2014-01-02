package com.slamtechnology.teeyai.prices;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.util.LogSetup;

public class PriceFarm implements Runnable {
	
	private static PriceFeed[] feeds;
	boolean running = true;
	boolean enabled = true;
	
	public static void main(String[] args){
		
		Logger l = LogSetup.tradesLog;
		l = LogSetup.backLog;
		
		System.out.println("Price farm call");
		//Session session = HibernateSessionFactory.getSession();
		//HibernateSessionFactory.closeSession();
		try {
			ServiceManager.getInstance().createPriceMessageService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ServiceManager.getInstance().createDB();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread t = new Thread(new PriceFarm(),"PriceFarm");
		t.start();
		System.out.println("Price farm started with process "+t.getId());
		
		
	}
	

	public void run() {
		
		long now = System.currentTimeMillis();
		feeds = ServiceManager.getInstance().getFeeds();
		HashMap<String,Long> feedChecktime = new HashMap<String,Long>(feeds.length);
		for(PriceFeed feed : feeds){
			feed.initialise();
			feed.setFeedTimes(now);
			//feedChecktime.put(feed.getName(), new Long(now));
		}
		//running = false;
		while(running){
			
			now = System.currentTimeMillis();
			
			//System.out.println("Waking at "+(new Date(now)));
			System.out.print(".");
			
			boolean oneActive = false;
			
			for(PriceFeed feed : feeds){
				
				if(enabled){
				
					System.out.println("Check feed "+feed.getDescription()+" ");
				
					if(now>feed.getStartTime().getTime()&&now<feed.getStopTime().getTime()){

						if(!feedChecktime.containsKey(feed.getName())){
							feedChecktime.put(feed.getName(), new Long(now));
						}
						Long nextTime = feedChecktime.get(feed.getName());
						System.out.println(feed.getName()+" Next feed update at "+(new Date(nextTime)));
						if(now>=nextTime.longValue()){
							//check feed
							Thread t = new Thread(new FeedThread(feed,now));
							t.start();
							feedChecktime.put(feed.getName(), new Long(nextTime.longValue()+feed.getIntervalInMillis()));
							System.out.println("Checking feed...");
						}
						
						oneActive=true;
						
					} else if (now>feed.getStopTime().getTime()){
						
						feedChecktime.remove(feed.getName());
						feed.setFeedTimes(now);
						
					} //else {
						//System.out.print("Feed opening hours are "+feed.getStartTime()+" to "+feed.getStopTime());
					//}
					//System.out.println();
				}
			}
			long delay = 1000;
			if(!oneActive){
				delay = 600000;
				System.out.println("Everyone's asleep at "+(new Date(now)));
				for(PriceFeed feed : feeds){
					System.out.println(feed.getName()+" wakes at "+feed.getStartTime());
					if(feed.getStartTime().getTime()<(now+delay)){
						delay = feed.getStartTime().getTime()-now-60000;
					}
				}
				if(delay<1000)delay=1000;
				String delayString = null;
				if(delay<60000){
					delayString = (delay/1000)+" secs";
				} else {
					delayString = (delay/60000)+" mins";
				}
				System.out.println("May as well sleep for "+delayString);
				System.out.println("");
				
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		
	}
	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	class FeedThread implements Runnable {
		PriceFeed feed;
		long now;
		public FeedThread(PriceFeed feed,long now){
			this.feed = feed;
			this.now=now;
		}
		public void run() {
			feed.getComponentPrices(now);
		}
	}
}
