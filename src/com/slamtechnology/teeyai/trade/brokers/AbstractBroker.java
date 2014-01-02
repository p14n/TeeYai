package com.slamtechnology.teeyai.trade.brokers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;
import com.slamtechnology.teeyai.trade.ExecutionListener;
import com.slamtechnology.teeyai.trade.Position;
import com.slamtechnology.teeyai.trade.Quote;

public abstract class AbstractBroker implements Broker {
	
	public AbstractBroker(BrokerageAuthenticator authenticator) {
		super();
		this.authenticator = authenticator;
	}



	protected BrokerageAuthenticator getAuthenticator() {
		return authenticator;
	}
	private Logger logger = Logger.getLogger(Broker.class.getName());
	
	/*protected abstract String getMarketPriceURL();
	protected abstract boolean requiresDealerResponse();
	protected abstract String getDealerResponseURL(String id);
	protected abstract String getRetrieveOrderURL(String orderid);
	protected abstract String getConfirmURL(Position p,double bid,double ask);
	protected abstract String getCreateStop(HashMap<String, String> params,int stopprice);*/

	private Position p;
	ExecutionListener listener;
	double tolerance;
	boolean open;
	BrokerageAuthenticator authenticator;
	protected Position myPosition(){
		return p;
	}


	
	protected Logger getLogger() {
		return logger;
	}
	public void setClosePosition(ExecutionListener ex, Position p,double tolerance) {
		this.listener = ex;
		this.p=p;
		this.tolerance=tolerance;
		open=false;
	}

	public void setOpenPosition(ExecutionListener ex, Position p, double tolerance) {
		this.listener = ex;
		this.p=p;
		this.tolerance=tolerance;
		open=true;
	}
	
	protected abstract Quote getMarketPrice() throws Exception;
	
	private void confirmFailed() {
		if(open){
			listener.confirmFailedOpen(p);
		} else {
			listener.confirmFailedClose(p);
		}
	}
	protected abstract Quote confirm(Quote q,boolean buy) throws Exception ;

	public String toString(){
		return getName();
	}

	public void execute() {
		if(listener==null){
			getLogger().info("No listener specified");
			listener = new InternalListener();
		}
		if(open){
			open();
		} else {
			close();
		}
	}

	private void close() {
		try {
			Quote bidOffer = closeTrade(p.getAmount(),p.isLong());
			if(p.isLong()){
				p.setExitPoint(bidOffer.getBid());
			} else {
				p.setExitPoint(bidOffer.getAsk());
			}
			listener.confirmClosePosition(p);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to confirm trade for "+getName());
			logger.log(Level.SEVERE, e.toString());
			listener.confirmFailedClose(p);
		}
	}



	protected abstract Quote closeTrade(double amount,boolean isLong);



	private void open() {
		
		
		Logger logger = getLogger();
		
		boolean continueExecute = true;
		
		Quote prices = null;
		
		try{
			prices = getMarketPrice();
		} catch (Exception e) {
			e.printStackTrace();
			continueExecute = false;
			logger.log(Level.SEVERE, "Failed to get market price for "+getName());
			logger.log(Level.SEVERE, e.toString());
		}
		
		if(continueExecute&&prices!=null&&prices.getBid()>0&&prices.getAsk()>0){
			
			boolean tradeIsLong = p.isOpen()?p.isLong():!p.isLong();

			double bid = prices.getBid();
			double ask = prices.getAsk();
			double offered=0;

			boolean priceAcceptable = false;

			if(tradeIsLong){
				offered = ask;
			} else {
				offered = bid;
			}

			if(tolerance==-1){
				//dont care what price is let me in!
				priceAcceptable = true;
			} else {
				
				double desired=0;
				
					desired = p.getEntryPoint();
				
			
				if(offered-tolerance<=desired&&offered+tolerance>=desired){
					priceAcceptable = true;
				}
					
			}
			
			if(!priceAcceptable){
				logger.log(Level.SEVERE, "Rejecting trade from "+getName());
				logger.log(Level.SEVERE, "Got bid "+bid+" ask "+ask+" wanted "+p.getEntryPoint());
				confirmFailed();
			} else {

				Quote bidOffer=null;
				continueExecute = false;
				try {
					bidOffer = confirm(prices,tradeIsLong);
					continueExecute = open?bidOffer.tradeIsOpened():bidOffer.tradeIsClosed();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Failed to confirm trade for "+getName());
					logger.log(Level.SEVERE, e.toString());
				}
				if(continueExecute){
						if(tradeIsLong){
							p.setEntryPoint(bidOffer.getAsk());
						} else {
							p.setEntryPoint(bidOffer.getBid());
						}
					p.setProfit();
						listener.confirmOpenPosition(p);
					
				} else {
					logger.info("Response indicates the trade was not executed");
					confirmFailed();
				}
			}
		} else {
			confirmFailed();
			logger.log(Level.SEVERE, "Stopping trade due to invalid market prices "+getName());
		}
		p=null;
	}
	
		
	public class InternalListener implements ExecutionListener{

		public void confirmClosePosition(Position p) {
			System.out.println("POSITION CLOSED");
		}

		public void confirmFailedClose(Position p) {
			System.out.println("POSITION FAILED TO CLOSE");
		}

		public void confirmFailedOpen(Position p) {
			System.out.println("POSITION FAILED TO OPEN");
		}

		public void confirmOpenPosition(Position p) {
			System.out.println("POSITION OPENED");
		}
		
	}

}
