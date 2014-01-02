package com.slamtechnology.teeyai.trade;


public interface BrokerageAuthenticator {
	
	public Object getSession();
	public void destroySession();
	public boolean isLoggingOn();

}
