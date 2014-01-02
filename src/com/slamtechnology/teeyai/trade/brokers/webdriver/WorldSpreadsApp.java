package com.slamtechnology.teeyai.trade.brokers.webdriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.SeleneseCommandExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.Quote;
import com.slamtechnology.util.Mailer;
import com.slamtechnology.util.Util;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public abstract class WorldSpreadsApp {
	
	private final static class AppHolder {
		public final static WorldSpreadsAdvancedApp app = new WorldSpreadsAdvancedApp();
	}

	public static WorldSpreadsApp get() {
		return AppHolder.app;
	}



	private static final int MAX_FAILED_QUOTES = 6;

	// Map<String,WebElement[]> bidAskComponents = new HashMap<String,
	// WebElement[]>();

	Logger log;

	Map<String, String> quoteIDS = new HashMap<String, String>();

	protected WorldSpreadsApp() {

		log = LoggerFactory.getLogger(Broker.class);
		createNewDriver();
	}

	protected void createNewDriver() {
		loggedIn = false;
		final WebDriver oldDriver = driver;
		if (oldDriver != null) {
			try {
				Mailer.send("damnpanache@gmail.com", "damnpanache@gmail.com",
						"TeeYai WorldspreadsApp hang", "");
				new Thread(new Runnable() {
					public void run() {
						oldDriver.quit();
					}
				}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		driver = new ChromeDriver();
		//driver = new HtmlUnitDriver(true);
		log.info("starting driver " + driver.getClass().getName());
		failedQuoteCount.set(0);
		if (username != null) {
			login(username, password);
		}

	}

	protected WebDriver driver;
	private volatile boolean loggedIn = false;
	// private volatile boolean quoting = false;
	private String username, password;
	AtomicInteger failedQuoteCount = new AtomicInteger();
	AtomicBoolean quoting = new AtomicBoolean(false);

	public void login(String user, String pass) {
		username = user;
		password = pass;
		loggedIn = false;
		log.info("logging in " + user + " " + pass);
		driver.get("https://www.worldspreads.com/en/home.aspx");
		driver.findElement(By.name("username")).sendKeys(user);
		driver.findElement(By.name("password")).sendKeys(pass);
		driver.findElement(
				By.className("submitbtn"))
				//By.xpath("id('login_panel')/div/div/table/tbody/tr[2]/td[3]/input"))
				.click();
		Util.waitFor(10);
		if (Util.isElementVisible(driver,
		// By.xpath("id('divMarketTab')"),
				By.id("divPopularMarket"), 30)) {
			log.info("Found popular markets");
			loggedIn = true;
			quoting.set(false);
		} else {
			log.info("Could not find popular markets");
			log.info(driver.getPageSource());
			Assert.fail("Popular markets not detected after 30 seconds - login fail");
		}
		// driver.switchTo().defaultContent();
	}

	public synchronized void logout() {
		if (driver != null && loggedIn) {
			try {
				driver.findElement(By.id("lblLogout")).click();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				driver.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			driver = null;
		}
		loggedIn = false;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}


	protected abstract WebElement[] getBidAskElements(String name);
	public Quote getQuote(String name) {
		return getQuote(name, false);
	}
	public Quote getQuote(String name,boolean waitIfBusy) {
		if (quoting.get()&&!waitIfBusy) {
			if (loggedIn) {
				int failures = failedQuoteCount.incrementAndGet();
				if (failures > MAX_FAILED_QUOTES) {
					createNewDriver();
				}
			}
			return null;
		}
		synchronized (quoting) {
			quoting.set(true);
			Quote q = new Quote();
			WebElement[] bidAsk = getBidAskElements(name);
			q.setAsk(Double.parseDouble(bidAsk[1].getText()));
			q.setBid(Double.parseDouble(bidAsk[0].getText()));
			log.info("Got quote id " + q.getBid() + " " + q.getAsk() + " for "
					+ name);
			quoting.set(false);
			failedQuoteCount.set(0);
			return q;
		}
	}

	public abstract boolean trade(String instrumentName, double amount, boolean buy);

	public Quote closeTrade(double amount,boolean isLong) {
		List<WebElement> list = driver.findElement(By.id("divOpenBets")).findElements(By.cssSelector("span.mid"));
		for(WebElement e:list){
			if("close".equalsIgnoreCase(e.getText())){
				e.click();
				break;
			}
		}
		if (!Util.isElementVisible(driver, By.id("txtStake"), 5)) {
			log.info("Amount field not detected");
			throw new RuntimeException("Could not close trade - amount field not detected");
		}
		log.info("Amount field detected");
		WebElement stake = 
			driver.findElement(By.id("txtStake"));
		stake.clear();
		stake.sendKeys(
				String.valueOf(amount));
		
		list = driver.findElement(By.id("btnConfirm")).findElements(By.cssSelector("span.mid"));
		for(WebElement e:list){
			if("close".equalsIgnoreCase(e.getText())){
				e.click();
				break;
			}
			throw new RuntimeException("Could not close trade - confirm button not detected");
		}
		if (!Util.isElementVisible(driver, By.id("spnOpenPrice"), 8)) {
			log.info("Closing price not detected");
			throw new RuntimeException("Could not close trade - Closing price not detected");
		}
		log.info("Closing price detected");
		Quote q = new Quote();
		double price = Double.parseDouble(driver.findElement(By.id("spnOpenPrice")).getText());
		if(isLong){
			q.setBid(price);
		} else {
			q.setAsk(price);
		}
		q.setTradeIsClosed(true);
		return q;

	}




}
