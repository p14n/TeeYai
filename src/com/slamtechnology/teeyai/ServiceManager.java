package com.slamtechnology.teeyai;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.derby.drda.NetworkServerControl;

import com.slamtechnology.teeyai.feeds.WorldSpreadsDemoFeed;
import com.slamtechnology.teeyai.indicators.ADXIndicator;
import com.slamtechnology.teeyai.indicators.BollingerBands;
import com.slamtechnology.teeyai.indicators.CCIOverDirectionalIndicator;
import com.slamtechnology.teeyai.indicators.CCIOverIndicator;
import com.slamtechnology.teeyai.indicators.CCIOverIndirectionalIndicator;
import com.slamtechnology.teeyai.indicators.ChannelIndicator;
import com.slamtechnology.teeyai.indicators.EMAIndicator;
import com.slamtechnology.teeyai.indicators.EMAPriceIndicator;
import com.slamtechnology.teeyai.indicators.HighADXInidcator;
import com.slamtechnology.teeyai.indicators.LowADXInidcator;
import com.slamtechnology.teeyai.indicators.MACDIndicator;
import com.slamtechnology.teeyai.indicators.MACDLineIndicator;
import com.slamtechnology.teeyai.indicators.MACDReversalLimit;
import com.slamtechnology.teeyai.indicators.PivotCandleStickIndicator;
import com.slamtechnology.teeyai.indicators.RVIIndicator;
import com.slamtechnology.teeyai.prices.CandleStick;
import com.slamtechnology.teeyai.prices.FeedComponent;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.teeyai.prices.PriceDAO;
import com.slamtechnology.teeyai.prices.PriceFeed;
import com.slamtechnology.teeyai.prices.PriceListener;
import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.BrokerMessageClient;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.TradeStrategy;
import com.slamtechnology.teeyai.trade.brokers.BrokerManager;

public class ServiceManager {
	
	private final static ServiceManager sm;
	private static ConnectionFactory factory;
	private static Context context;
	
	
	static {
		sm = new ServiceManager();
	}
	
	public static ServiceManager getInstance(){
		
		return sm;
	}
	
	PriceFeed[] feeds;
	FeedComponent[] components;
	Broker[] brokers;
	HashMap<String,TeeYai> tys;
	HashMap<String,ArrayList<TeeYai>> monitors;
	Indicator[] indicators;
	BrokerManager brokerManager;
	static String brokerUrl="tcp://localhost:61616";
	BrokerService broker;


	private ServiceManager(){
		
		//feeds = getFeeds();
		//Thread t = new Thread(pf);
		//t.start();
		tys = new HashMap<String, TeeYai>();
		monitors = new HashMap<String,ArrayList<TeeYai>>();


	}
	
	public TeeYai getTeeYai(String name){
		if(!tys.containsKey(name)){
			TradeManager tm = TeeYaiDAO.getTradeManager(name);
			System.out.println("Searching for "+name+" got tm "+(tm==null?"null":tm.getTeeYaiName()));
			if(tm==null){
				tm= new TradeManager();
				tm.setTeeYaiName(name);
				TeeYaiDAO.saveTradeManager(tm);
				System.out.println("New tm");
			}
			TeeYai ty = new TeeYai(name,tm);
			System.out.println("New ty");
			tys.put(name, ty);
		}
		System.out.println("getting ty");
		return tys.get(name);
	}
	public void removeTeeYai(TeeYai ty){
		tys.remove(ty.getName());
	}

	public PriceFeed[] getFeeds(){
		
		if(feeds==null){
			feeds = new PriceFeed[1];
			//feeds[0] = new ETradeDemoFeed("c:\\temp\\etradedem");
			//feeds[0] = new ETradeLiveFeed("c:\\temp\\etradelive");
			//feeds[0] = new BBCFTSEFeed();
			feeds[0] = new WorldSpreadsDemoFeed();
		}

		return feeds;
	}
	public FeedComponent[] getFeedComponents(){
		if(components==null){
			ArrayList<FeedComponent> c = new ArrayList<FeedComponent>();
			for(PriceFeed feed:getFeeds()){
				for(FeedComponent fc:feed.getComponents()){
					c.add(fc);
				}
			}
			components = c.toArray(new FeedComponent[c.size()]);
		}
		return components;
	}
	public Broker[] getBrokers(){
		
		if(brokers==null){
			brokers = new Broker[2];
			brokers[0] = new BrokerMessageClient("Worldspreads demo FTSE broker", 3);
			brokers[1] = new BrokerMessageClient("Worldspreads demo Wall St broker", 4);
			
			/*brokers[0] = new ETradeDemoFTSEBroker();
			brokers[1] = new ETradeDemoDowBroker();
			brokers[2] = new ETradeLiveFTSEBroker();
			brokers[3] = new ETradeLiveDowBroker();*/
			
		}

		return brokers;
	}
	public Indicator[] getIndicators(){
		
		if(indicators==null){
			indicators = new Indicator[13];
			indicators[0] = new EMAIndicator();
			indicators[1] = new ChannelIndicator();
			indicators[2] = new MACDIndicator();
			indicators[3] = new RVIIndicator();
			indicators[4] = new BollingerBands();
			indicators[5] = new MACDLineIndicator();
			indicators[6] = new MACDReversalLimit();
			indicators[7] = new LowADXInidcator();
			indicators[8] = new HighADXInidcator();
			indicators[9] = new EMAPriceIndicator();
			indicators[10] = new CCIOverDirectionalIndicator();
			indicators[11] = new CCIOverIndirectionalIndicator();
			indicators[12] = new PivotCandleStickIndicator();
		}
		
		return indicators;
	}
	

	public void removeFeedMonitor(String feedname,TeeYai monitor){
		if(monitors.containsKey(feedname)){
			monitors.get(feedname).remove(monitor);
		}
	}
	public void addFeedMonitor(String feedname,TeeYai monitor){
		if(!monitors.containsKey(feedname)){
			monitors.put(feedname, new ArrayList<TeeYai>());
		}
		monitors.get(feedname).add(monitor);
		
		addListener(feedname, new PriceListener(feedname));

	}
	public void addListener(String qname,MessageListener m){
		try {
			
			Connection connection = getFactory().createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(qname);
		    MessageConsumer receiver = session.createConsumer(destination);
		    
		    receiver.setMessageListener(m);
		    connection.start();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void updatePrice(String feedname,Price price){
		if(monitors.containsKey(feedname)){
			ArrayList<TeeYai> feedMonitors = monitors.get(feedname);
			for(TeeYai m : feedMonitors ){
				m.updatePrice(feedname,price);
			}
		}
	}
	public List<Price> getPriceHistory(String feedname, long start,long end){
		return PriceDAO.getPriceHistory(feedname,start,end);
	}

	/*public long saveTradeStrategy(TradeStrategy ts){
		
		return TeeYaiDAO.saveTradeStrategy(ts);
		
	}*/	

	public long addTradeStrategy(long trademanagerid,TradeStrategy ts){
		
		return TeeYaiDAO.addTradeStrategy(trademanagerid, ts);
		
	}
	public long updateTradeStrategy(TradeStrategy ts){
		
		return TeeYaiDAO.updateTradeStrategy(ts);
		
	}
	public void deleteTradeStrategy(long strategyid){
		
		TeeYaiDAO.deleteTradeStrategy(strategyid);
		
	
	}
	private static ConnectionFactory getFactory(){
		if(factory==null){
			setupFactory();
		}
		return factory;
	}
	private static synchronized void setupFactory(){
		if(factory==null){
			factory = new ActiveMQConnectionFactory(brokerUrl);
		}
	}

/*	private static synchronized void setupFactory(){
		if(factory==null){
		    Hashtable properties = new Hashtable();
		    properties.put(Context.INITIAL_CONTEXT_FACTORY,"org.exolab.jms.jndi.InitialContextFactory");
		    properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
			try {
				context = new InitialContext(properties);
				factory = (ConnectionFactory) context.lookup("ConnectionFactory");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean executeBrokerTrades(String teeYaiName){
		boolean sent=false;
	    try {
			Connection connection = getFactory().createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = (Destination) context.lookup("brokerManager");
		    connection.start();
		    MessageProducer sender = session.createProducer(destination);
		    TextMessage message = session.createTextMessage(teeYaiName);
		    sender.send(message);    
		    sender.close();
		    session.close();
		    connection.close();
		    sent=true;
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return sent;
	}
	public boolean registerBroker(String teeYaiName,Broker broker){
		 if(brokerManager==null){
			 brokerManager = new BrokerManager();
			try {
					
				if(factory==null){
					setupFactory();
				}
				Connection connection = factory.createConnection();
				Destination destination = (Destination) context.lookup("brokerManager");
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageConsumer receiver = session.createConsumer(destination);
				    
				receiver.setMessageListener(brokerManager);
				connection.start();
				    
			} catch (Exception e) {
				e.printStackTrace();
			}

		 }
		if(broker==null){
			brokerManager.unregisterBroker(teeYaiName);
		} else {
			brokerManager.registerBroker(teeYaiName, broker);
		}
		return false;
	}
	*/
	public void createPriceMessageService() throws Exception{
		try {
			Connection c = getFactory().createConnection();
			c.close();
		} catch (Exception e) {
			createBroker(); 
		}
	}
	private synchronized void createBroker() throws Exception { 
		if(broker==null){
			broker = new BrokerService(); 
			broker.setPersistent(false); 
			broker.setUseJmx(false); 
			broker.addConnector(brokerUrl); 
			broker.start();		
		}
	}
	public void createDB(){
		try {
			String nsURL="jdbc:derby://localhost:1527/TeeYaiDB";  
			java.util.Properties props = new java.util.Properties();
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			DriverManager.getConnection(nsURL, props);
		} catch (Exception e){
			startDB();
		}
	}
	private synchronized void startDB(){
		NetworkServerControl server;
		try {
			server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
			server.start(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, Map<Integer, Set<Integer>>> getAllAvailableBackTestDates() {
		HashMap<Integer, Map<Integer,Set<Integer>>> datesToInclude = new HashMap<Integer, Map<Integer,Set<Integer>>>();
		List<Price> prices = PriceDAO.getAllPriceHistory();
		if(prices!=null){
			Calendar cal = Calendar.getInstance();
			int index=0;
			for(Price p:prices){
				if(index%100==0){
					cal.setTimeInMillis(p.getTime());
					
					int year = cal.get(Calendar.YEAR);
					int month = cal.get(Calendar.MONTH);
					int day = cal.get(Calendar.DAY_OF_MONTH);

					if(!datesToInclude.containsKey(year)){
						datesToInclude.put(year, new HashMap<Integer, Set<Integer>>());
					}
					if(!datesToInclude.get(year).containsKey(month)){
						datesToInclude.get(year).put(month, new HashSet<Integer>());
					}
					datesToInclude.get(year).get(month).add(day);

				}
			}
		}
		return datesToInclude;
	}
	public CandleStick getYesterdaysClosingPrices(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return getClosingPrices(cal.getTime());
	}
	private CandleStick getClosingPrices(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Properties p = new Properties();
			p.load(ClassLoader.getSystemResourceAsStream("ohlc.properties"));
			String ohlc = p.getProperty(sdf.format(date));
			StringTokenizer st = new StringTokenizer(ohlc,",");
			CandleStick cs = new CandleStick();
			while(st.hasMoreTokens()){
				cs.add(new Price(Double.parseDouble(st.nextToken())));
			}
			return cs;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 9);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.YEAR, 2011);
		System.out.println(new ServiceManager().getYesterdaysClosingPrices(cal.getTimeInMillis()));
	}

}
