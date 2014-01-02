package com.slamtechnology.teeyai.trade;

import java.util.logging.Logger;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.slamtechnology.teeyai.ServiceManager;

public class BrokerMessageClient implements Broker,MessageListener {

	ExecutionListener l;
	Position p;
	Logger log;
	String name;
	long id;
	
	public BrokerMessageClient(String name,long id) {
		super();
		this.id = id;
		this.name = name;
		ServiceManager.getInstance().addListener(getName()+"_ack", this);
		log = Logger.getLogger(Broker.class.getName());
	}

	public long getID() {
		return id;
	}

	public void setOpenPosition(ExecutionListener ex, Position p,
			double tolerance) {
		l = ex;
		p.setOpen(true);
		p.setTolerance(tolerance);
		this.p = p;
	}

	public String getName() {
		return name;
	}

	public void setClosePosition(ExecutionListener ex, Position p,
			double tolerance) {
		l = ex;
		p.setOpen(false);
		p.setTolerance(tolerance);
		this.p = p;
	}

	public void execute() {
		BrokerMessenger.sendMessage(getName(), p);
	}

	public void onMessage(Message m) {
		if(m instanceof TextMessage){
			TextMessage t = (TextMessage)m;
			
			log.info("Received message from broker");

			try {
				String text = t.getText(); 
				log.info(text);
				Position p = Position.fromMessageString(text);
				if(p.isOpen()){
					if(p.isSuccess()){
						log.info("Confirm open");
						l.confirmOpenPosition(p);
					} else {
						log.info("Confirm failed open");
						l.confirmFailedOpen(p);
					}
				} else {
					if(p.isSuccess()){
						log.info("Confirm close");
						l.confirmClosePosition(p);
					} else {
						log.info("Confirm failed close");
						l.confirmFailedClose(p);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			log.info("Ignoring message of type "+m.getClass().getName());
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
