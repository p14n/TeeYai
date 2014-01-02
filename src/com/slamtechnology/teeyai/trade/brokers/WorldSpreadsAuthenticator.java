package com.slamtechnology.teeyai.trade.brokers;

import com.slamtechnology.teeyai.trade.BrokerageAuthenticator;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsAdvancedApp;
import com.slamtechnology.teeyai.trade.brokers.webdriver.WorldSpreadsApp;

public abstract class WorldSpreadsAuthenticator implements BrokerageAuthenticator {

	volatile Boolean setup = Boolean.FALSE;
	volatile boolean loggingOn = false;

	public Object getSession() {
		System.out.println("*******************************************");
		System.out.println("GET SESSION");
		System.out.println("Is logged in ? "+WorldSpreadsApp.get().isLoggedIn());
		System.out.println("Setup "+(setup.booleanValue()?"true":"false"));
		System.out.println("*******************************************");
		
		if(!WorldSpreadsAdvancedApp.get().isLoggedIn()){
			loggingOn = true;
			synchronized (setup) {
				if(!WorldSpreadsAdvancedApp.get().isLoggedIn()&&!setup.booleanValue()){
					setupConnection();
					setup = Boolean.TRUE;
				}
			}
			loggingOn = false;
		}
		System.out.println("*******************************************");
		System.out.println("END GET SESSION");
		System.out.println("Is logged in? "+WorldSpreadsApp.get().isLoggedIn());
		System.out.println("Setup "+(setup.booleanValue()?"true":"false"));
		System.out.println("*******************************************");
		return null;
	}
	
	private void setupConnection(){
		System.out.println("*******************************************");
		System.out.println("CREATE SESSION");
		System.out.println("*******************************************");
		WorldSpreadsApp.get().login(getUsername(),getPassword());
		System.out.println("*******************************************");
		System.out.println("CREATED SESSION");
		System.out.println("*******************************************");
		
		/*synchronized (setup) {

			try {
				HttpUnitOptions.setScriptingEnabled( false );
				session = new WebConversation();
				HttpUnitOptions.setLoggingHttpHeaders(true);
				session.getClientProperties().setAcceptCookies(true);
				session.getClientProperties().setAutoRedirect(false);
				session.getClientProperties().setUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_7; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16");
				File cookieFile = new File(getCookieFile());
				List<String> cookie = new ArrayList<String>();
				if(cookieFile.exists()){
					
					FileReader fr = new FileReader(cookieFile);
					BufferedReader br = new BufferedReader(fr);
					String line = null;
					while((line = br.readLine())!=null){
						cookie.add(line);
					}
					br.close();
					fr.close();
					
					setCookie(cookie);

					
				} else {
					
					WebResponse wr0 = session.getResponse(getLoginFormURL());
					System.out.println(wr0.getText());
					WebForm wf = wr0.getForms()[0];
					wf.setParameter("username",getUsername());
					wf.setParameter("password",getPassword());
					
					WebResponse wr1 = wf.submit();
					System.out.println(wr1.getText());
					String[] cookies = wr1.getHeaderFields("Set-Cookie");
					cookie = Arrays.asList(cookies);
					System.out.println("Cookie "+cookie);
					
					FileWriter fw = new FileWriter(cookieFile);
					for(String line:cookie){
						fw.write(line+"\n");
					}
					fw.flush();
					fw.close();
					System.out.println("*******************************************");
					System.out.println("WROTE COOKIE "+cookie);
					System.out.println("*******************************************");
					//setup = new Boolean(true);
					
					setCookie(cookie);
					
					wf = wr1.getForms()[0];
					wr1 = wf.submit();
					System.out.println("*******************************************");
					System.out.println("INITIALISED");
					System.out.println("*******************************************");
					System.out.println(wr1.getText());
					
					//wr1 = session.getResponse("https://www.worldspreads.com/tradeengine2/xeqtgatewayv3/Advanced.aspx");
					//System.out.println(wr1.getText());
					
					
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		//} */
		
	}
	public void destroySession() {
		WorldSpreadsApp.get().logout();
		setup = new Boolean(false);
	}

	public boolean isLoggingOn() {
		return loggingOn;
	}
	
	protected abstract String getLoginFormURL();
	protected abstract String getUsername();
	protected abstract String getPassword();


}
