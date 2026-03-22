/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.command;

import com.catya.interpreter.exception.BrowserCommandException;
import com.catya.interpreter.exception.TimeoutCommandException;


public interface BrowserCommand {
	
	//Throws BrowserCommandException
	void open(String browser) throws BrowserCommandException;
	void close(String browser) throws BrowserCommandException;
	void pause(long time) throws BrowserCommandException;
	void printScreen(int itemNo) throws BrowserCommandException;
	void refresh() throws BrowserCommandException;
	void input(String locator, String locatorValue, String value) throws BrowserCommandException;
	void select(String locator, String locatorValue, String option, String[] values, int delay) throws BrowserCommandException;
	void deselect(String locator, String locatorValue, String option, String[] values) throws BrowserCommandException;
	String getText() throws BrowserCommandException;
	void clear(String locator, String locatorValue) throws BrowserCommandException;
	boolean verify(String locator, String locatorValue, String condition, String valueToVerify) throws BrowserCommandException;
	boolean isElementEnabled(String locator, String locatorValue, String condition, String valueToVerify) throws BrowserCommandException;
	boolean isElementSelected(String locator, String locatorValue, String condition, String valueToVerify) throws BrowserCommandException;
	boolean isElementVisible(String locator, String locatorValue, String condition, String valueToVerify) throws BrowserCommandException;
	
	//Throws BrowserCommandException and TimeoutCommandException
	void navigate(String url, int timeout) throws BrowserCommandException, TimeoutCommandException;
	void click(String locator, String value, int timeout) throws BrowserCommandException, TimeoutCommandException;
	void doubleClick(String locator, String value, int timeout) throws BrowserCommandException, TimeoutCommandException;
	void waitVisible(String locator, String locatorValue, int timeout) throws BrowserCommandException, TimeoutCommandException;
	void waitClickable(String locator, String locatorValue, int timeout) throws BrowserCommandException, TimeoutCommandException;
	
	//No Exception
	void quit();
}
