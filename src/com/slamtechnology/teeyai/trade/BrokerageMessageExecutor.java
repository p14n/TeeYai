package com.slamtechnology.teeyai.trade;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class BrokerageMessageExecutor implements MessageListener {

	Logger log;
	Broker broker;
	public BrokerageMessageExecutor(Broker broker) {
		super();
		this.broker = broker;
		log = Logger.getLogger(Broker.class.getName());
	}
	public void onMessage(Message m) {
		if(m instanceof TextMessage){
			TextMessage t = (TextMessage)m;
			try {
				log.info("Received message from app");
				log.info(t.getText());
				Position p = Position.fromMessageString(t.getText());
				
				ExecutionListener l = new ExecutionListener() {
					
					public void confirmOpenPosition(Position p) {
						p.setSuccess(true);
						sendMessage(p);
					}
					
					public void confirmFailedOpen(Position p) {
						p.setSuccess(false);
						sendMessage(p);
					}
					
					public void confirmFailedClose(Position p) {
						p.setSuccess(false);
						sendMessage(p);
					}
					
					public void confirmClosePosition(Position p) {
						p.setSuccess(true);
						sendMessage(p);
					}
				};
				if(p.isOpen()){
					log.info("Open position");
					broker.setOpenPosition(l, p, p.getTolerance());
				} else {
					log.info("Close position");
					broker.setClosePosition(l, p, p.getTolerance());
				}
				log.info("Executing");
				broker.execute();
				log.info("Executing complete");
			} catch (Exception e) {
				StringWriter w = new StringWriter();
				e.printStackTrace(new PrintWriter(w));
				log.severe(w.toString());
				throw new RuntimeException(e);
			}
		} else {
			log.info("Ignoring message of type "+m.getClass().getName());
		}
	}
	
	private void sendMessage(Position p){
		BrokerMessenger.sendMessage(broker.getName()+"_ack", p);
	}

}
