package com.slamtechnology.teeyai.trade.brokers.webdriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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

import com.slamtechnology.teeyai.trade.Broker;
import com.slamtechnology.teeyai.trade.Quote;
import com.slamtechnology.util.Mailer;
import com.slamtechnology.util.Util;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class WorldSpreadsAdvancedApp extends WorldSpreadsApp {

	Logger log;

	protected WorldSpreadsAdvancedApp() {

		log = Logger.getLogger(Broker.class.getName());
		createNewDriver();
	}

	private WebElement findMarketRow(String name) {
		int row = 0;
		WebElement popularMarkets = driver.findElement(By
				.id("divPopularMarket"));
		log.info("Looking for my market row "+name);
		while (true) {
			WebElement marketRow = popularMarkets.findElement(By.id("yui-rec"
					+ row++));
			WebElement marketNameCol = marketRow.findElement(By
					.className("yui-dt-col-MarketName"));
			WebElement marketNameDiv = marketNameCol.findElement(By
					.tagName("div"));
			String text = marketNameDiv.getText();
			log.info("Found " + text);
			if (text.startsWith(name)) {
				log.info("Found my market row");
				return marketRow;
			}
			if(row>100) return null;;
		}
	}

	protected WebElement[] getBidAskElements(String name) {
		if (!quoteIDS.containsKey(name)) {
			WebElement row = findMarketRow(name);
			WebElement askDiv = row.findElement(By
					.cssSelector("td.yui-dt-col-Ask div div"));
			String qid = askDiv.getAttribute("id").replace("div-ask-mq", "");
			quoteIDS.put(name, qid);
			log.info("Found my market quote id " + qid);
		}
		WebElement askDiv = driver.findElement(By.id("div-ask-mq"
				+ quoteIDS.get(name)));
		WebElement bidDiv = driver.findElement(By.id("div-bid-mq"
				+ quoteIDS.get(name)));
		return new WebElement[] { bidDiv, askDiv };
	}

	private void clickTrade(String name) {
		log.info("clicking trade for " + name + " quote id "
				+ quoteIDS.get(name));
		driver.findElement(By.id("div-buttonTrade-mq" + quoteIDS.get(name)))
				.click();
		if (!Util.isElementVisible(driver, By.id("btnSell"), 5)) {
			log.info("Sell button not detected");
			Assert.fail("Sell button not detected after 5 seconds - trade fail");
		} else {
			log.info("Sell button detected");
		}

	}

	public boolean trade(String instrumentName, double amount, boolean buy) {
		synchronized (quoting) {
			try {
				quoting.set(true);
				clickTrade(instrumentName);
				log.info("Confirm trade  - sending amount " + amount);
				driver.findElement(By.id("txtStake")).sendKeys(
						String.valueOf(amount));
				log.info("Confirm trade  - clicking trade button ");
				driver.findElement(By.id(buy ? "btnBuy" : "btnSell")).click();
				log.info("Looking for confirmation header");

				if (Util.isElementVisible(driver,
						By.cssSelector("div#divTradeSuccessful"), 10)) {
					log.info("Found confirmation header");
					log.info("Trade success confirmed");
					return true;
				}
				log.info("Trade success not confirmed");
				log.info(driver.getPageSource());
			} catch (Exception e) {
				StringWriter w = new StringWriter();
				e.printStackTrace(new PrintWriter(w));
				log.severe(w.toString());
			} finally {
				quoting.set(false);
			}
			return false;
		}
	}

}
