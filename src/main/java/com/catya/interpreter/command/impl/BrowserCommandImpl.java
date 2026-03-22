/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.command.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.catya.browserdriver.BrowserDriverFactory;
import com.catya.interpreter.command.BrowserCommand;
import com.catya.interpreter.exception.BrowserCommandException;
import com.catya.interpreter.exception.ExceptionCause;
import com.catya.interpreter.exception.TimeoutCommandException;

public class BrowserCommandImpl implements BrowserCommand{
	
	private volatile String text;
	
	private static Logger LOG = Logger.getLogger(BrowserCommandImpl.class.getName());
	
	private void setTimeout(int timeout) {
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
		if (driver != null) {
			driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(timeout));
		}
	}
	
	private void setDefaultTimeout() {
		setTimeout(10);
	}
	
	@Override
	public void open(String browser) throws BrowserCommandException {
		if (BrowserDriverFactory.getInstance().isBrowserOpened(browser)) {
			LOG.info(browser + " browser was already opened.");
			close(browser);
		} 
		
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver(browser);
		if (driver == null) {
			setTimeout(10);
			System.out.println("Browser [" + browser + "] is not supported.");
		} else {
			LOG.info("Opening a " + browser + " browser.");
		}
	}
	
	@Override
	public void close(String browser) {
		LOG.info("Closing " + browser + " browser.");
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver(browser);
		driver.quit();
		BrowserDriverFactory.getInstance().removeDriver(browser);
	}
	
	@Override
	public void quit() {
		BrowserDriverFactory.getInstance().quit();
	}

	@Override
	public void click(String locator, String locatorValue, int timeout) throws BrowserCommandException, TimeoutCommandException {
		WebElement element = findElement(locator, locatorValue);
		try {
			setTimeout(timeout);
			element.click();
		} catch (TimeoutException e) {
			throw new TimeoutCommandException("Click element timeout", ExceptionCause.TIMEOUT);
		} catch (Exception e) {
			throw new BrowserCommandException("Element [" + locatorValue + "] not found!", ExceptionCause.ELEMENT_NOT_FOUND);
		} finally {
			setDefaultTimeout();
		}
	}
	
	@Override
	public void doubleClick(String locator, String locatorValue, int timeout) throws BrowserCommandException, TimeoutCommandException {
		
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
		Actions act = new Actions(driver);

		WebElement element = findElement(locator, locatorValue);
		try {
			setTimeout(timeout);
			act.doubleClick(element).build().perform();
		} catch (TimeoutException e) {
			throw new TimeoutCommandException("Double Click element timeout", ExceptionCause.TIMEOUT);
		} catch (Exception e) {
			throw new BrowserCommandException("Element [" + locatorValue + "] not found!", ExceptionCause.ELEMENT_NOT_FOUND);
		} finally {
			setDefaultTimeout();
		}
	}	
	
	@Override
	public void navigate(String url, int timeout) throws BrowserCommandException, TimeoutCommandException {
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
		final String paramUrl = url;
		try {
			setTimeout(timeout);
			driver.get(url);
		} catch (TimeoutException e) {
			throw new TimeoutCommandException("Navigate takes too long to load", ExceptionCause.TIMEOUT);
		} catch (Exception e) {
			throw new BrowserCommandException("Cannot navigate to url: " + paramUrl, ExceptionCause.URL_ERROR);
		} finally {
			setDefaultTimeout();
		}
	}

	@Override
	public void input(String locator, String locatorValue, String value) throws BrowserCommandException {
		
		WebElement element = findElement(locator, locatorValue);
		
		final String paramValue = value;
		final String paramLocatorValue = locatorValue;
		try {
			element.sendKeys(paramValue);
		} catch (Exception e) {
			throw new BrowserCommandException("Element locator: " + paramLocatorValue + " is not found", ExceptionCause.ELEMENT_NOT_FOUND);
		}
	}

	@Override
	public boolean verify(String locator, String locatorValue,
			String condition, String conditionValue) throws BrowserCommandException {
		
		boolean result = false;
		
		try {
			setTimeout(10);
			WebElement element = findElement(locator, locatorValue);
			
			String lvalue = "";
			
			if (element.getTagName().equalsIgnoreCase("input")) {
				lvalue = element.getAttribute("value");
			} else {
				lvalue = element.getText();
			}
			
			this.text = lvalue;
			
			if (condition.equals("=")) {
				if (lvalue.equals(conditionValue)) {
					result = true;
				}
			}
		} catch (Exception e) {
			this.text = e.getMessage();
			LOG.severe(e.getMessage());
		} finally {
			setDefaultTimeout();
		}
		return result;
	}
	
	@Override
	public String getText() throws BrowserCommandException {
		return text;
	}
	
	private WebElement findElement(String locator, String locatorValue) throws BrowserCommandException {
		WebElement element = null;
		
		try {
			element = BrowserDriverFactory.getInstance().findElement(locator, locatorValue);
	    } catch (Exception e) {
			throw new BrowserCommandException("Element [" + locatorValue + "] is not found", ExceptionCause.ELEMENT_NOT_FOUND);
		}
		return element;
	}

	@Override
	public void clear(String locator, String locatorValue)
			throws BrowserCommandException {
		
		WebElement element = findElement(locator, locatorValue);
		element.clear();
	}

	@Override
	public void pause(long time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void waitVisible(String locator, String locatorValue, int timeout)
			throws BrowserCommandException, TimeoutCommandException {
		By elementLocator = null;
		
		if (locator.equalsIgnoreCase("id")) {
			elementLocator = By.id(locatorValue);
		} else if (locator.equalsIgnoreCase("xpath")) {
			elementLocator = By.xpath(locatorValue);
		} else if (locator.equalsIgnoreCase("name")) {
			elementLocator = By.name(locatorValue);
		}
		
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
		
		WebDriverWait waitElement = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeout));
		
		try {
			waitElement.until(ExpectedConditions.visibilityOfElementLocated(elementLocator));
		} catch (TimeoutException e) {
			throw new TimeoutCommandException("waitVisible timedout", ExceptionCause.TIMEOUT);
		} catch (Exception e) {
			throw new BrowserCommandException("waitVisible throws an exception", ExceptionCause.UNKNOWN_ERROR);
		}
	}

	@Override
	public void waitClickable(String locator, String locatorValue, int timeout)
			throws BrowserCommandException, TimeoutCommandException {
		By elementLocator = null;
		
		if (locator.equalsIgnoreCase("id")) {
			elementLocator = By.id(locatorValue);
		} else if (locator.equalsIgnoreCase("xpath")) {
			elementLocator = By.xpath(locatorValue);
		} else if (locator.equalsIgnoreCase("name")) {
			elementLocator = By.name(locatorValue);
		}
		
		try {
			WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
			WebDriverWait waitElement = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeout));
			waitElement.until(ExpectedConditions.elementToBeClickable(elementLocator));
		} catch (TimeoutException e) {
			throw new TimeoutCommandException("waitVisible timedout", ExceptionCause.TIMEOUT);
		} catch (Exception e) {
			throw new BrowserCommandException("waitVisible throws an exception", ExceptionCause.UNKNOWN_ERROR);
		}
	}

	@Override
	public boolean isElementEnabled(String locator, String locatorValue, String condition, String valueToVerify)
			throws BrowserCommandException {
		boolean ret = false;
		try {
			setTimeout(10);
			WebElement element = findElement(locator, locatorValue);
			if (condition.equals("=")) {
				this.text = Boolean.toString(element.isEnabled());
				
				if (this.text.equals(valueToVerify)) {
					ret = true;
				}
			}
		} catch (Exception e) {
			this.text = Boolean.toString(false);
			LOG.severe(e.getMessage());
		} finally {
			setDefaultTimeout();
		}
		return ret;
	}
	
	@Override
	public boolean isElementSelected(String locator, String locatorValue, String condition, String valueToVerify)
			throws BrowserCommandException {
		
		boolean ret = false;
		try {
			setTimeout(10);
			WebElement element = findElement(locator, locatorValue);
			if (condition.equals("=")) {
				this.text = Boolean.toString(element.isSelected());
				
				if (this.text.equals(valueToVerify)) {
					ret = true;
				}
			}
		} catch (Exception e) {
			this.text = Boolean.toString(false);
			LOG.severe(e.getMessage());
		} finally {
			setDefaultTimeout();
		}
		return ret;
	}
	
	@Override
	public boolean isElementVisible(String locator, String locatorValue, String condition, String valueToVerify)
			throws BrowserCommandException {
		boolean ret = false;
		try {
			setTimeout(10);
			WebElement element = findElement(locator, locatorValue);
			if (condition.equals("=")) {
				this.text = Boolean.toString(element.isDisplayed());
				
				if (this.text.equals(valueToVerify)) {
					ret = true;
				}
			}
		} catch (Exception e) {
			this.text = Boolean.toString(false);
			LOG.severe(e.getMessage());
		} finally {
			setDefaultTimeout();
		}
		return ret;
	}
	
	@Override
	public void printScreen(int itemNo) throws BrowserCommandException {
		try {
			String fileName = String.format("./screenshots/Failed_item_%d.jpg", itemNo);
			WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
			File screenshot = ((TakesScreenshot)driver).
                    getScreenshotAs(OutputType.FILE);
			File destination=new File(fileName);
			FileUtils.copyFile(screenshot, destination);
		} catch (Exception e) {
			throw new BrowserCommandException("Unknown Exception.", ExceptionCause.UNKNOWN_ERROR);
		}
	}
	
	@Override
	public void refresh() throws BrowserCommandException {
		try {
			BrowserDriverFactory.getInstance().getDriver().navigate().refresh();
		} catch (Exception e) {
			throw new BrowserCommandException ("Unknown exception occurred.", ExceptionCause.UNKNOWN_ERROR);
		}
	}
	
	@Override
	public void select(String locator, String locatorValue, String optionName, String[] options, int delay) throws BrowserCommandException {
		WebDriver driver = BrowserDriverFactory.getInstance().getDriver();
		WebElement element= findElement(locator, locatorValue);
	
		if (element.getTagName().equalsIgnoreCase("table")) {
			List<WebElement> elements = element.findElements(By.tagName("td"));
			
			Actions builder = new Actions(driver);
			Actions keyDown;
			String os = System.getProperty("os.name", "generic").toLowerCase();
			if (os.indexOf("mac") >= 0|| os.indexOf("darwin") >= 0) {
				keyDown = builder.keyDown(Keys.COMMAND);
			} else {
				keyDown = builder.keyDown(Keys.CONTROL);
			}
			
			for (String option: options) {
				try {
					int index = Integer.parseInt(option.trim());
					keyDown.click(elements.get(index));
					Action multiSelect = keyDown.build();
					multiSelect.perform();
					Thread.sleep(delay * 1000);
				} catch (Exception e) {
					throw new BrowserCommandException("Select command in table requires index.", ExceptionCause.UNKNOWN_ERROR);
				}
			}
		} else {
			Select se=new Select(element);
			if (options[0].equalsIgnoreCase("none")) {
				se.deselectAll();
			} else {
			
				for (String option: options) {
					if (optionName.equalsIgnoreCase("name")) {
						se.selectByVisibleText(option.trim());
					} else {
						try {
							se.selectByIndex(Integer.parseInt(option.trim()));
						} catch (Exception e) {
							throw new BrowserCommandException ("Expects number after index", ExceptionCause.UNKNOWN_ERROR);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void deselect(String locator, String locatorValue, String optionName, String[] options) throws BrowserCommandException {
		
		WebElement element= findElement(locator, locatorValue);
		
		Select se=new Select(element);
		
		if (options[0].equalsIgnoreCase("all")) {
			se.deselectAll();
		} else {
		
			for (String option: options) {
				if (optionName.equalsIgnoreCase("name")) {
					se.deselectByVisibleText(option);
				} else {
					try {
						se.deselectByIndex(Integer.parseInt(option));
					} catch (Exception e) {
						throw new BrowserCommandException ("Expects number after index", ExceptionCause.UNKNOWN_ERROR);
					}
				}
			}
		}
	}	
}
