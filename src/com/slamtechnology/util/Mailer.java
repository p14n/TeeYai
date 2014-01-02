package com.slamtechnology.util;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.code.samples.xoauth.XoauthAuthenticator;
import com.sun.mail.smtp.SMTPTransport;

public class Mailer {

	
	public static void main(String[] args){
		try {
			send("damnpanache@gmail.com","damnpanache@gmail.com","test","msg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static SMTPTransport smtp;
	
	private synchronized static SMTPTransport getSMTP() throws Exception{
		if(smtp==null){
			XoauthAuthenticator.initialize();
			smtp = XoauthAuthenticator.connectToSmtp(getSession());
		}
		return smtp;
	}
	
	public static void send(String from, String to, String subject,String content)
		throws Exception {
		
		try {
		// Construct the message
		Session s = getSession();
		Message msg = new MimeMessage(s);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		msg.setText(content);
		// Send the message
		//Transport.send(msg);
		getSMTP().sendMessage(msg,msg.getAllRecipients());
	} catch (Exception e) {
		e.printStackTrace();
	}

	}
	
	private static Session session = null; 
	private static Session getSession(){
		if(session==null){
			// Create a mail session
			/*java.util.Properties props = new java.util.Properties();
			props.put("mail.smtp.host", "localhost");
			props.put("mail.smtp.port", "25");
			session = Session.getDefaultInstance(props, null);*/
			session = XoauthAuthenticator.getSession(true);
		}
		return session;
	}

}
