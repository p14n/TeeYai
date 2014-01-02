package com.slamtechnology.teeyai.trade;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class BrokerMessenger {
	private static ConnectionFactory factory;
	static String brokerUrl="tcp://localhost:61616";
	static Logger log = Logger.getLogger(Broker.class.getName()); 
	
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

	public static void sendMessage(String queueName,Position p){
	    try {
			   // System.out.println("Create connection");
				Connection connection = getFactory().createConnection();
			    //System.out.println("Create session");
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			   // System.out.println("Find destination");
				Destination destination = session.createQueue(queueName);
			   // System.out.println("Start connection");
			    connection.start();
			    //System.out.println("Create message");
			    MessageProducer sender = session.createProducer(destination);
			    //System.out.println("Send message");
			    String text = p.toMessageString();
			    TextMessage message = session.createTextMessage(text);
			    sender.send(message);    
			    System.out.println("Message sent");
			    sender.close();
			    session.close();
			    connection.close();
			    log.info("Sent broker message "+text);
			} catch (JMSException e) {
				StringWriter s = new StringWriter();
				e.printStackTrace(new PrintWriter(s));
			    log.info("Send broker message failed "+s.toString());
			}

	}


}
