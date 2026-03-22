/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.browserdriver;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class BrowserDriverFactory {
	private static BrowserDriverFactory instance;
	HashMap<String, WebDriver> webDriverFactory;
	private static Logger LOG = Logger.getLogger(BrowserDriverFactory.class.getName());
	
	private BrowserDriverFactory() {
		init();
	}
	
	public static BrowserDriverFactory getInstance() {
		if (instance == null) {
			instance = new BrowserDriverFactory();
		}
		return instance;			
	}
	
	private void init() {
		webDriverFactory = new HashMap<String, WebDriver>();
	}
	
	public WebDriver getDriver(String browser) {
		
		WebDriver driver = webDriverFactory.get(browser.toLowerCase());
		if (driver == null) {
			if (browser.equalsIgnoreCase("firefox")) {
				driver = new FirefoxDriver();
			} else if (browser.equalsIgnoreCase("chrome")) {
				LOG.info("Browser is chrome");
				driver = new ChromeDriver();
			} else if (browser.equalsIgnoreCase("ie")) {
				driver = new InternetExplorerDriver();
			}
			if (driver != null) {
				webDriverFactory.put(browser.toLowerCase(), driver);
			} else {
				LOG.info("Webdriver is null");
			}
		}
		
		return driver;
	}
	
	public WebDriver getDriver() {
		WebDriver driver = null;
		
		for (Entry<String, WebDriver> we: webDriverFactory.entrySet()) {
			if ((driver = we.getValue()) != null)
				break;
		}
		
		return driver;
	}
	
	public boolean isBrowserOpened(String browser) {
		boolean result = false;
		
		WebDriver driver = webDriverFactory.get(browser.toLowerCase());

		if (driver != null) {
			result = true;
		}
		
		return result;
	}
	
	public void removeDriver(String browser) {
		webDriverFactory.remove(browser.toLowerCase());
	}
	
	public void quit() {
		for (Entry<String, WebDriver> we: webDriverFactory.entrySet()) {
			we.getValue().quit();
		}
		webDriverFactory.clear();
	}
	
	public WebElement findElement(String locator, String element) {
		WebElement webElement = null; 
		
		for (Entry<String, WebDriver> we: webDriverFactory.entrySet()) {
			if (locator.equalsIgnoreCase("id")) {
				webElement = we.getValue().findElement(By.id(element));
			} else if (locator.equalsIgnoreCase("xpath")) {
				webElement = we.getValue().findElement(By.xpath(element));
			} else if (locator.equalsIgnoreCase("name")) {
				webElement = we.getValue().findElement(By.name(element));
			} else if (locator.equalsIgnoreCase("class")) {
				webElement = we.getValue().findElement(By.className(element));
			}
			
			if (webElement != null) {
				break;
			}
		}
		return webElement;
	}
}
