package com.slamtechnology.teeyai.prices;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class PriceMessenger {
	
	private static ConnectionFactory factory;
	static String brokerUrl="tcp://localhost:61616";
	
	public static void main(String args[]){
	    System.out.println("Start test");
		sendPriceMessage("ETradeFtseDemo", 6000);
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
	
	public static void sendPriceMessage(String feedName,double price){

	    try {
		   // System.out.println("Create connection");
			Connection connection = getFactory().createConnection();
		    //System.out.println("Create session");
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		   // System.out.println("Find destination");
			Destination destination = session.createQueue(feedName);
		   // System.out.println("Start connection");
		    connection.start();
		    //System.out.println("Create message");
		    MessageProducer sender = session.createProducer(destination);
		    //System.out.println("Send message");
		    TextMessage message = session.createTextMessage(String.valueOf(price));
		    sender.send(message);    
		    System.out.println("Message sent");
		    sender.close();
		    session.close();
		    connection.close();
		    //System.out.println("Sessions closed");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

}
