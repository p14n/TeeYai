package com.slamtechnology.util;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class Util {
	
	public static void waitFor(int timeOutInSeconds){
		waitFor(null,timeOutInSeconds);
	}
	public static void waitFor(PageCondition con, int timeOutInSeconds){
		long start = System.currentTimeMillis();
		boolean stillWaiting = true;
		while (stillWaiting){
			stillWaiting = System.currentTimeMillis()<(start+(timeOutInSeconds*1000));
			if(con!=null&&stillWaiting){
				try {
					stillWaiting = !con.isMet();
				} catch (Exception e) {}
			}
			if(stillWaiting){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static boolean isElementVisible(final WebDriver driver,final By by,int secondsToWait) {
		waitFor(new PageCondition() {
			public boolean isMet() {
				return driver.findElement(by)!=null;
			}
		}, secondsToWait);
		WebElement element = null;
		try {
			element = driver.findElement(by); 
		} catch (Exception e){}
		return element!=null;
	}
	public static void verifyTextContainedInElement(WebDriver driver,By by, String text,
			String failureMessage) {
		WebElement we  = driver.findElement(by);
		String elementText = we.getText();
		assertTrue(failureMessage+" (found "+elementText+")",elementText.contains(text) );
	}
	public static String getInnerHTML(WebDriver driver,WebElement e){
		return (String)((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", e);
	}
	public static String getAttributeValueFromHTML(String name,String html){
		int startOf = html.indexOf(name)+name.length()+2;
		int endOf = html.indexOf("\"",startOf);
		return html.substring(startOf, endOf);

	}
	public static void executeJS(WebDriver driver,String script){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(script);

	}
	public static void executeOnclickJS(WebDriver driver,WebElement e){
		String htmlOfLink = getInnerHTML(driver, e);
		String jsCall = getAttributeValueFromHTML("onclick", htmlOfLink);
		executeJS(driver, jsCall);
	}


}
