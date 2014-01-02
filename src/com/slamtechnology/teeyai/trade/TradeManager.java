package com.slamtechnology.teeyai.trade;
import static com.slamtechnology.teeyai.ui.Toaster.pop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.prices.Price;
import com.slamtechnology.util.Mailer;

public class TradeManager implements ExecutionListener{
	
	private static SimpleDateFormat sdf;

	static{
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	String teeYaiName;
	
	Long managerID;
	HashMap<String,ArrayList<Indicator>> indicators =new HashMap<String,ArrayList<Indicator>>();

	ArrayList<TradeStrategy> tradeStrategies = new ArrayList<TradeStrategy>();
	ArrayList<Position> openPositions = new ArrayList<Position>();
	ArrayList<Position> recentPositions = new ArrayList<Position>();
	//ArrayList<Position> justExecuted;
	
	HashMap<String , Double> lastPrices = new HashMap<String , Double>();
	HashMap<String , Double> minPrices = new HashMap<String , Double>();
	HashMap<String , Double> maxPrices = new HashMap<String , Double>();
	long maxTime=0;
	
	private TradeConfiguration config;

	private synchronized TradeConfiguration config(){
		return config;
	}
	public synchronized void setConfig(TradeConfiguration config){
		this.config=config;
	}
	
	int currentTrades=0;
	boolean trading;
	boolean buyOpen = false;
	int ignoreInstruction = -1;
	boolean ignoreTrendAfterExit=true;
	boolean closeAll = false;
	Logger logger ;
	
	boolean ignoreNextInstruction=false;
	boolean ignoreFirstInstruction=true;
	double currentProfit=0;

	
	public String getOpeningTime() {
		return config().openingTime;
	}
	public void setOpeningTime(String openingTime) {
		if(openingTime!=null){
			StringTokenizer st = new StringTokenizer(openingTime,"-");
			if(st.hasMoreTokens()){
				String start = st.nextToken();
				this.config().openingTime = start;
				StringTokenizer sti = new StringTokenizer(start,":");
				if(sti.hasMoreTokens()){
					config().openingHour = Integer.parseInt(sti.nextToken());
					config().openingMin = Integer.parseInt(sti.nextToken());
				}
			}
			if(st.hasMoreTokens()){
				String end = st.nextToken();
				this.config().closingTime = end;
				StringTokenizer sti = new StringTokenizer(end,":");
				if(sti.hasMoreTokens()){
					config().closingHour = Integer.parseInt(sti.nextToken());
					config().closingMin = Integer.parseInt(sti.nextToken());
				}
			}

		}
	}
	boolean sellOpen = false;

	private double triggerStopAt=0;
	public TradeManager(){
		logger = LoggerFactory.getLogger(TradeManager.class);
		setConfig(new TradeConfiguration());
		reset(true,true);
		//logger = Logger.getLogger(TradeManager.class.getName());

	}
	
	private boolean isPastOpeningTime(long time){
		if(config().openingTime!=null&&config().openingHour>0){
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GB"));
			cal.setTimeInMillis(time);
			int hr = cal.get(Calendar.HOUR_OF_DAY);
			int mn = cal.get(Calendar.MINUTE);
			if(hr==config().openingHour){
				if(mn<config().openingMin){
					logger.info("Price time "+hr+":"+mn+" is before opening time "+config().openingTime);
					return false;
				}
			} else if (hr<config().openingHour){
				logger.info("Price time "+hr+":"+mn+" is before opening time "+config().openingTime);
				return false;
			}
			if(hr==config().closingHour){
				if(mn>=config().closingMin){
					logger.info("Price time "+hr+":"+mn+" is after closing time "+config().closingTime);
					return false;
				}
			} else if (hr>config().closingHour){
				logger.info("Price time "+hr+":"+mn+" is after closing time "+config().closingTime);
				return false;
			}
		}
		return true;
	}
	public void addIndicator(String priceFeedName, Indicator indicator){
		if(!indicators.containsKey(priceFeedName)){
			indicators.put(priceFeedName, new ArrayList<Indicator>());
		}
		indicators.get(priceFeedName).add(indicator);
	}
	public void addStrategy(TradeStrategy strategy){
		System.out.println("Adding new strategy "+strategy.getName());
		tradeStrategies.add(strategy);
	}
	public void deleteStrategy(TradeStrategy strategy){
		updateStrategy(strategy,false);
	}
	public Broker getBroker() {
		return config().broker;
	}
	public Long getManagerID() {
		return managerID;
	}

	public ArrayList<Position> getOpenPositions() {
		return openPositions;
	}	

	public ArrayList<Position> getRecentPositions() {
		return recentPositions;
	}
	public double getSpread() {
		return config().spread;
	}
	public String getTeeYaiName() {
		return teeYaiName;
	}
	public ArrayList<TradeStrategy> getTradeStrategies() {
		return tradeStrategies;
	}

	public boolean isTrading() {
		return trading;
	}
	public void notifyOfFailedClose(Broker broker,Position p){
		setTrading(false);
		
		String text = (p.isLong()?"Short":"Buy")+" at "+p.getEntryPoint()+"\n"+p.toMessageString();
		try {
			Mailer.send("damnpanache@gmail.com","damnpanache@gmail.com" , "Failed to CLOSE a trade "+broker.getName(), text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void notifyOfFailedOpen(Broker broker,Position p){
		setTrading(false);
		String text = (p.isLong()?"Buy":"Short")+" at "+p.getEntryPoint()+"\n"+p.toMessageString();
		try {
			Mailer.send("damnpanache@gmail.com","damnpanache@gmail.com" , "Failed to open a trade "+broker.getName(), text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeIndicator(String priceFeedName,Indicator indicator){
		
		if(indicators.containsKey(priceFeedName)){
			ArrayList<Indicator> ids = indicators.get(priceFeedName);
			ids.remove(indicator);
			if(ids.size()==0){
				indicators.remove(indicator.getFeedName());
			}
		}
	
	}

	public void setBroker(Broker broker) {
		this.config().broker = broker;
	}

	public void setManagerID(Long managerID) {
		this.managerID = managerID;
	}

	public void setSpread(double spread) {
		this.config().spread = spread;
	}

	public void setTeeYaiName(String teeYaiName) {
		this.teeYaiName = teeYaiName;
	}

	public void setTradeStrategies(List strategies){
		tradeStrategies = new ArrayList<TradeStrategy>(strategies==null?0:strategies.size());
		tradeStrategies.addAll(strategies);
	}

	public void setTrading(boolean trading) {
		this.trading = trading;
		reset(false,true);
	}
	
	public void update(String feedname,Price pObj){
		
		double price = pObj.getPrice();
		
		boolean pastOpeningTime = isPastOpeningTime(pObj.getTime());
		boolean overMinSwing = swingGreaterThanMin(feedname,price);

		if(trading){
			
			//Double lastPrice = lastPrices.get(feedname);
			
			if(price>0){
				
				logger.info("Trademan recived price "+price+" "+new Date(pObj.getTime()));

	
				lastPrices.put(feedname, new Double(price));
				
				boolean overallBuy = false;
				boolean overallSell = false;
				boolean overallShortTrade = false;
				boolean overallCover = false;			
				String openingStrategy = null;
				String closingStrategy = null;

				ArrayList<Indicator> indicatorList = indicators.get(feedname);


				for(TradeStrategy tradeStrategy:tradeStrategies){
					
					if(tradeStrategy!=null&&tradeStrategy.isEnabled()){
					
						logger.info("Trademan check strategy "+tradeStrategy.getName()+" ("+tradeStrategy.getExecutionType()+")");

						boolean buy = true;
						boolean sell = true;
						boolean shortTrade = true;
						boolean cover = true;
					
						boolean buyStrategyExists = false;
						boolean sellStrategyExists = false;
						boolean shortStrategyExists = false;
						boolean coverStrategyExists = false;
						
						if(indicatorList!=null){
							for(Indicator indicator : indicatorList){
								
								switch(tradeStrategy.getExecutionType()){
								
								case TradeStrategy.BUY:
									if((!indicator.isBuy())&&tradeStrategy.isIndicator(indicator.getInstanceName())){
										buy = false;
									}
									buyStrategyExists=true;
	
									break;
								case TradeStrategy.SELL:
									if((!indicator.isSell())&&tradeStrategy.isIndicator(indicator.getInstanceName())){
										sell = false;
									}
									sellStrategyExists=true;
	
									break;
								case TradeStrategy.SHORT:
									if((!indicator.isShort())&&tradeStrategy.isIndicator(indicator.getInstanceName())){
										shortTrade = false;
									}
									shortStrategyExists=true;
	
									break;
								case TradeStrategy.COVER:
									if((!indicator.isCover())&&tradeStrategy.isIndicator(indicator.getInstanceName())){
										cover = false;
									}
									coverStrategyExists=true;
	
									break;
								
								}
								logger.info("Indicator "+(tradeStrategy.isIndicator(indicator.getInstanceName())?"on":"not on")+" strategy "+indicator.getInstanceName()+
										" buy?"+indicator.isBuy()+" "+
										" sell?"+indicator.isSell()+" "+
										" short?"+indicator.isShort()+" "+
										" cover?"+indicator.isCover()
										);
	
							}
						}

						if(buy&&buyStrategyExists){
							overallBuy = true;
							openingStrategy = tradeStrategy.getName();
						}
						if(sell&&sellStrategyExists){
							overallSell = true;
							closingStrategy = tradeStrategy.getName();
						}
						if(shortTrade&&shortStrategyExists){
							overallShortTrade = true;
							openingStrategy = tradeStrategy.getName();
						}
						if(cover&&coverStrategyExists){
							overallCover = true;
							closingStrategy = tradeStrategy.getName();
						}
					}
				}

				//Find positions to close
				closePositions(price, pObj.getTime(), closingStrategy, overallSell, overallCover);

				logger.info("Trademan buy?"+overallBuy+" sell?"+overallSell+" ignore?"+ignoreNextInstruction);

				if(config().targetProfit>0&&currentProfit>=config().targetProfit){
					logger.info("Target profit reached");
				} else if(currentTrades>=config().maxTrades){
					logger.info("Max number of trades ("+currentTrades+") done");
				} else if (!pastOpeningTime){
					logger.info("Outside trading hours");
				} else if (!overMinSwing){
					logger.info("Market range too small to enter");
				} else {
			
					//ignore the first trade advice
					if(ignoreNextInstruction){
						if(ignoreInstruction == -1){
							if(overallBuy){
								ignoreInstruction=TradeStrategy.BUY;
								logger.info("Setting next ignore buy");
							}
							if(overallShortTrade){
								ignoreInstruction=TradeStrategy.SHORT;
								logger.info("Setting next ignore short");
							}
						}
						if(overallBuy){
							if(ignoreInstruction == TradeStrategy.BUY){
								overallBuy=false;
								logger.info("Ignoring buy");
							} else {
								logger.info("Ending ignore");
								ignoreNextInstruction=false;
								ignoreInstruction = -1;
							}

						} else if (overallShortTrade){
							if(ignoreInstruction == TradeStrategy.SHORT) {
								overallShortTrade=false;
								logger.info("Ignoring short");
							} else {
								logger.info("Ending ignore");
								ignoreNextInstruction=false;
								ignoreInstruction = -1;
							}
						} else {
							ignoreNextInstruction = false;
							ignoreInstruction = -1;
						}
					}
					if(overallBuy||overallShortTrade){
						openPosition(feedname,openingStrategy,price,overallBuy,pObj.getTime());
					}

				}
			} else {
				logger.info("Ignoring 0 price");
			}
		} else {
			logger.info("Trade manager disabled");
		}
	}
	
	private boolean swingGreaterThanMin(String feedName,double price) {
		if(config().minSwing<=0.0) return true;
		if(minPrices.get(feedName)==null||minPrices.get(feedName)>price){
			minPrices.put(feedName, price);
		}
		if(maxPrices.get(feedName)==null||maxPrices.get(feedName)<price){
			maxPrices.put(feedName, price);
		}
		double swing = (maxPrices.get(feedName)-minPrices.get(feedName));
		return swing>config().minSwing;
	}
	public int getMinSwing() {
		return config().minSwing;
	}
	public void setMinSwing(int minSwing) {
		this.config().minSwing = minSwing;
	}
	public void buy(String feedname){
		openPosition(feedname, "Opened manually", getLastPrice(feedname), true, System.currentTimeMillis());
	}
	public void sell(String feedname){
		openPosition(feedname, "Opened manually", getLastPrice(feedname), false, System.currentTimeMillis());
	}
	
	private void openPosition(String feedname,String openingStrategy,double price,boolean buy,long time){
		Position p = null;
		
		if(!buyOpen&&!sellOpen){
			
			if(buy){

				p = new Position(feedname,price+(config().spread/2),true);
				p.setHighPoint(price);
				if(config().stopSpread>0){
					p.setStop(price-config().stopSpread);
				}
				
			} else {
				
				p = new Position(feedname,price-(config().spread/2),false);
				p.setLowPoint(price);
				if(config().stopSpread>0){
					p.setStop(price+config().stopSpread);
				}
			}
			
			p.setOpeningStrategy(openingStrategy);
			p.setAmount(config().amount);
			p.setOpened(new Date(time));
			if(config().broker!=null){
				config().broker.setOpenPosition(this,p,config().priceTolerance);
				//ServiceManager.getInstance().executeBrokerTrades(teeYaiName);
				config().broker.execute();
			} else {
				this.confirmOpenPosition(p);
			}
			if(p.isLong()){
				buyOpen=true;
			} else {
				sellOpen=true;
			}
			logger.info((p.isLong()?"Buying ":"Shorting ")+sdf.format(new Date(time))+" "+price+" tradeManager.update buy?"+buy+" short?"+(!buy)+" open?"+openPositions.size()+" closed?"+recentPositions.size());
			
			
		}
	}
	
	private void closePositions(double price,long time,String closingStrategy,boolean overallSell,boolean overallCover){
		
		if(openPositions.size()>0){
			logger.debug("Examining open positions to find exit points");
		}
		for(int pi = 0; pi<openPositions.size();pi++){//Position p: openPositions){
			
			Position p = openPositions.get(pi);
			
			if(p.isLong()){
				p.setProfit((price-p.getEntryPoint())-(config().spread/2));
				if(config().stopSpread>0&&p.getStop()<price-config().stopSpread){
					p.setStop(price-config().stopSpread);
				}
			} else {
				p.setProfit((p.getEntryPoint()-price)-(config().spread/2));
				if(config().stopSpread>0&&(p.getStop()==0||p.getStop()>price+config().stopSpread)){
					p.setStop(price+config().stopSpread);
				}
			}
			//logger.debug("Setting profit: price {}, entry {}, spread {}, profit {}",
			//		new Double[]{price,p.getEntryPoint(),config().spread,p.getProfit()});
			
			if(triggerStopAt>0&&(p.getProfit()+config().spread)>=triggerStopAt){
				config().stop = -1;
			}
			
			boolean closeCondition = false;
			
			if(!p.hasClosingStrategy()){ //don't try and close trades that are closing 

				logger.debug("Position entry {}, profit {}",p.getEntryPoint(),p.getProfit());
				
				if((overallSell&&p.isLong())||(overallCover&&!p.isLong())){
					//Normal close
					closeCondition=true;
					p.setClosingStrategy(closingStrategy);

				} else if(config().stopSpread>0&&p.isLong()&&(price<=p.getStop())){
					closeCondition=true;
					p.setClosingStrategy("Trailing stop");
					ignoreNextInstruction=true;
				} else if(config().stopSpread>0&&!p.isLong()&&(price>=p.getStop())){
					closeCondition=true;
					p.setClosingStrategy("Trailing stop");						
					ignoreNextInstruction=true;
				} else if((config().stop!=0)&&
					( p.getProfit()+config().stop <= 0)){
						closeCondition=true;
						p.setClosingStrategy("Stopped");
						ignoreNextInstruction=true;
				} else if (config().profitTake>0&&p.getProfit()>=config().profitTake){
					closeCondition=true;
					p.setClosingStrategy("Take Profit");				
					ignoreNextInstruction=true;
				} else if (config().targetProfit>0&&(currentProfit+p.getProfit()>=config().targetProfit)){
					closeCondition=true;
					p.setClosingStrategy("Target Profit");						
				} else if (closeAll){
					closeCondition = true;
					p.setClosingStrategy("Manually closed");
					p.setTolerance(-1);
				}
				
				if(maxTime>0&&p.getOpened().getTime()+maxTime>System.currentTimeMillis()){
					closeCondition=true;
					p.setClosingStrategy("Timed out");						
				}
				
				logger.debug("Closing strategy {}",new Object[]{p.getClosingStrategy()});


			}
			if(closeCondition){
				double exitPrice = price;
				if(p.isLong()){
					exitPrice = exitPrice - (config().spread/2);
				} else {
					exitPrice = exitPrice + (config().spread/2);
				}
				p.setExitPoint(exitPrice);
				p.setClosed(new Date(time));
				if(config().broker!=null){
					config().broker.setClosePosition(this, p, -1);
					config().broker.execute();
				} else {
					confirmClosePosition(p);
					pi--;
					if(p.isLong()){
						buyOpen=false;
					} else {
						sellOpen=false;
					}
				}

				logger.info("Closing "+sdf.format(new Date(time))+" long?"+p.isLong()+" "+price+" tradeManager.update sell?"+overallSell+" cover?"+overallCover+" open?"+openPositions.size()+" closed?"+recentPositions.size()+" strategy "+p.getClosingStrategy());
				logger.info("Profit "+p.getProfit());
				
				currentProfit = currentProfit + p.getProfit();
				
				if(ignoreTrendAfterExit&&!ignoreNextInstruction){
					String ignore = null;
					ignoreNextInstruction=true;
					if(p.isLong()){
						ignoreInstruction = TradeStrategy.BUY;
						ignore="buy";
					} else {
						ignoreInstruction = TradeStrategy.SHORT;
						ignore="short";
					}
					logger.info("Exiting trend - ignore next "+ignore);
				}
				
			} else {

				if(p.getHighPoint()<price){
					p.setHighPoint(price);
				}
				if(p.getLowPoint()>price){
					p.setLowPoint(price);
				}

			}
			
		}

	}

	public void updateStrategy(TradeStrategy strategy){
		updateStrategy(strategy,true);
	}
	private void updateStrategy(TradeStrategy strategy,boolean replace){
		boolean added = false;
		int count=0;
		if(strategy.getId()>0){
			for(int i=0;i<tradeStrategies.size();i++){
				TradeStrategy ts = tradeStrategies.get(i);
				if(ts!=null&&ts.getId()==strategy.getId()){
					tradeStrategies.remove(count);
					if(replace){
						tradeStrategies.add(count, strategy);
						added=true;
					}
				}
				count++;
			}
		}
		if(!added&&replace){
			addStrategy(strategy);
		}
	}
	public double getProfitTake() {
		return config().profitTake;
	}
	public void setProfitTake(double profitTake) {
		this.config().profitTake = profitTake;
	}
	public void reset(boolean defaultValues,boolean wipePositions){
		//Logger logger = Logger.getLogger("com.slamtechnology.teeyai.trade.TradeManager");
		logger.info("Trade manager is being reset");
		if(wipePositions){
			openPositions.clear();
			recentPositions.clear();
		} else {
			recentPositions.addAll(openPositions);
			openPositions.clear();
		}
		closeAll = false;
		buyOpen = false;
		sellOpen = false;
		currentProfit = 0;
		lastPrices = new HashMap<String , Double>();
		minPrices = new HashMap<String , Double>();
		maxPrices = new HashMap<String , Double>();
		ignoreInstruction = -1;
		currentTrades=0;
		if(ignoreFirstInstruction){
			ignoreNextInstruction=true;
		} else {
			ignoreNextInstruction=false;
		}
		if(defaultValues){
			ignoreTrendAfterExit=true;
			config().spread=1;
			config().amount=1;
			config().targetProfit=12;
			config().priceTolerance=1;
			ignoreFirstInstruction=false;
			ignoreNextInstruction=false;
			config().maxTrades=1;
		}
	}
	private double getLastPrice(String feedname){
		double price= 0;
		if(lastPrices!=null&&lastPrices.size()>0){
			Double p = lastPrices.get(feedname);
			if(p!=null) return p;
		}
		return price;
	}
	public void closeAll() {
		this.closeAll = true;
		closePositions(0, System.currentTimeMillis(), null, false, false);
	}
	public boolean isReadyToTrade(){
		Logger logger = LoggerFactory.getLogger(TradeManager.class);
		boolean ready=true;
		if(buyOpen){
			ready=false;
			logger.info("Buy already open");
		}
		if(sellOpen){
			ready=false;
			logger.info("Sell already open");
		}
		if(!trading){
			ready=false;
			logger.info("Trade manager disabled");
		}
		if(ignoreNextInstruction){
			logger.info("Ignoring next trade");
		}
		if(config().targetProfit>0&&config().targetProfit<=currentProfit){
			ready=false;
			logger.info("Target profit reached");
		}
		if(ignoreFirstInstruction){
			ready=false;
			if(ignoreInstruction==TradeStrategy.BUY){
				logger.info("Ignoring all instructions to buy");
			} else if(ignoreInstruction==TradeStrategy.SHORT){
				logger.info("Ignoring all instructions to short");
			} else {
				logger.info("Ignoring next instruction: not yet set");
			}
		}
		return ready;
	}
	public double getTargetProfit() {
		return config().targetProfit;
	}
	public void setTargetProfit(double targetProfit) {
		this.config().targetProfit = targetProfit;
	}
	public void confirmClosePosition(Position p) {
		currentTrades++;
		try {
			int index = openPositions.indexOf(p);
			if(index>-1){
				Position existing = openPositions.get(index);
				existing.setExitPoint(p.getExitPoint());
				existing.setProfit();
				p = existing;
			}
			openPositions.remove(p);
			recentPositions.add(p);
			ServiceManager.getInstance().getTeeYai(teeYaiName).updateRecentTradeOnCharts(p);
			if(p.isLong()){
				buyOpen=false;
			} else {
				sellOpen=false;
			}
			pop("Position closed at "+p.getExitPoint()+" with a profit of "+p.getProfit());
		} catch (Exception e){
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.info(w.toString());
		}

	}
	public void confirmFailedClose(Position p) {
		try {
			notifyOfFailedClose(config().broker, p);
			pop("Failed to close position");
		} catch (Exception e){
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.info(w.toString());
		}
	}
	public void confirmFailedOpen(Position p) {
		System.out.println("confirmFailedOpen1");
		try {
			notifyOfFailedOpen(config().broker, p);
			System.out.println("confirmFailedOpen2"+p.isLong());
			if(p.isLong()){
				System.out.println("confirmFailedOpen3");
				buyOpen=false;
			} else {
				System.out.println("confirmFailedOpen1");
				sellOpen=false;
			}
			pop("Failed to open position");
		} catch (Exception e){
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.info(w.toString());
		}
	}
	public void confirmOpenPosition(Position p) {
		logger.info("confirmOpenPosition "+p);
		openPositions.add(p);
		try {
			ServiceManager.getInstance().getTeeYai(teeYaiName).updateRecentTradeOnCharts(p);
			pop("Position opened at "+p.getEntryPoint());
		} catch (Exception e){
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.info(w.toString());
		}
	}
	public double getAmount() {
		return config().amount;
	}
	public void setAmount(double amount) {
		this.config().amount = amount;
	}
	public double getStopSpread() {
		return config().stopSpread;
	}
	public void setStopSpread(double stopSpread) {
		this.config().stopSpread = stopSpread;
	}
	public double getStop() {
		return config().stop;
	}
	public void setStop(double stop) {
		this.config().stop = stop;
	}
	public double getPriceTolerance() {
		return config().priceTolerance;
	}
	public void setPriceTolerance(double priceTolerance) {
		this.config().priceTolerance = priceTolerance;
	}
	public void setIgnoreFirstInstruction(boolean ignoreFirstInstruction) {
		this.ignoreFirstInstruction = ignoreFirstInstruction;
	}
	public int getMaxTrades() {
		return config().maxTrades;
	}
	public void setMaxTrades(int maxTrades) {
		this.config().maxTrades = maxTrades;
	}
	public String getClosingTime() {
		return config().closingTime;
	}
}
