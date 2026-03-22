/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import com.catya.interpreter.command.BrowserCommand;
import com.catya.interpreter.command.impl.BrowserCommandImpl;
import com.catya.interpreter.constants.CommonConstants;
import com.catya.interpreter.exception.BrowserCommandException;
import com.catya.interpreter.exception.SyntaxException;
import com.catya.interpreter.exception.TimeoutCommandException;
import com.catya.interpreter.tokenizer.TokenManager;
import com.catya.interpreter.tokenizer.TokenType;


public class DSLInterpreter implements IInterpreter {
	private TokenManager tm;
	private BrowserCommand browserCommand;
	private boolean executeStatus;
	private String actualResult;
	private ResourceBundle resourceBundle;
	private Map<String, String> globalVariables = new TreeMap<String, String>();
	private static Logger LOG = Logger.getLogger(DSLInterpreter.class.getName());
	private Stack<LoopData> stack = new Stack<>();
	
	public int interpretCommand(String command, Integer ip) throws SyntaxException, TimeoutCommandException {
		
		int result = CommonConstants.PASSED;
		
		tm = new TokenManager(command);
		browserCommand = new BrowserCommandImpl();
		
		while (tm.getToken() != null) {
			switch(tm.getTokenType()) {
			case CALL:
				break;
			case CLEAR:
				executeClear();
				break;
			case CLICK:
				executeClick();
				break;
			case CLOSE:
				executeClose();
				break;
			case DELIMITER:
				break;
			case DESELECT:
				executeDeselect();
				break;
			case DOUBLECLICK:
				executeDoubleClick();
				break;
			case EXIT:
				executeExit();
				break;
			case FI:
				executeFi();
				break;
			case IF:
				result = executeIf();
				break;
			case INPUT:
				executeInput();
				break;
			case IS_ELEMENT_ENABLED:
				result = executeTestElement(TokenType.IS_ELEMENT_ENABLED.string());
				break;
			case IS_ELEMENT_SELECTED:
				result = executeTestElement(TokenType.IS_ELEMENT_SELECTED.string());
				break;
			case IS_ELEMENT_VISIBLE:
				result = executeTestElement(TokenType.IS_ELEMENT_VISIBLE.string());
				break;
			case LOAD:
				executeLoad();
				break;
			case LOOP:
				executeLoop(ip);
				break;
			case NAVIGATE:
				executeNavigate();
				break;
			case OPEN:
				executeOpen();
				break;
			case OTHER:
				break;
			case PAUSE:
				executePause();
				break;	
			case PRINTSCREEN:
				executePrintScreen();
				break;
			case POOL:
				result = executePool(ip);
				break;
			case QUIT:
				executeQuit();
				break;
			case QUOTED:
				break;
			case REFRESH:
				executeRefresh();
				break;
			case SELECT:
				executeSelect();
				break;
			case VARIABLE:
				break;
			case VERIFY:
				result = executeVerify();
				break;
			case WAIT_CLICKABLE:
				result = executeWaitElementClickable();
				break;
			case WAIT_VISIBLE:
				result = executeWaitElementVisible();
				break;
			}
		}
		return result;
	}
	
	private void executeOpen() throws SyntaxException{
		String token;
		String browser;
		
		token = tm.getToken();
		
		if (token == null || !token.equalsIgnoreCase("browser")) {
			throw new SyntaxException(
					token == null ? "missing [browser] in command" : "Unknown command: [" + token + "]");
		}
		
		token = tm.getToken();
		
		if (token == null || !token.equals("=")) {
			throw new SyntaxException(
					token == null ? "missing [=] in command" : "Expects [=] near " + token);
		}
		
		browser = tm.getToken();
		
		if (browser == null || browser.trim().isEmpty()) {
			throw new SyntaxException("Missing browser value.");
		}
		
		try {
			browserCommand.open(browser);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
	}
	
	private void executeClose () throws SyntaxException{
		String token;
		String browser;
		
		token = tm.getToken();
		
		if (token == null || !token.equalsIgnoreCase("browser")) {
			throw new SyntaxException(
					token == null ? "missing [browser] in command" : "Unknown command: [" + token + "]");
		}
		
		token = tm.getToken();
		
		if (token == null || !token.equals("=")) {
			throw new SyntaxException(
					token == null ? "missing [=] in command" : "Expects [=] near " + token);
		}
		
		browser = tm.getToken();
		
		if (browser == null || browser.trim().isEmpty()) {
			throw new SyntaxException("Missing browser value.");
		}
		
		try {
			browserCommand.close(browser);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
	}
	
	private int executeNavigate () throws SyntaxException {
		String token;
		String url;
		int ret = 1;
		
		token = tm.getToken();
		
		if (token == null || !token.equalsIgnoreCase("url")) {
			throw new SyntaxException(
					token == null ? "missing [url] in command" : "Unknown command: [" + token + "]");
		}
		
		token = tm.getToken();
		
		if (token == null || !token.equals("=")) {
			throw new SyntaxException(
					token == null ? "missing [=] in command" : "Expects [=] near " + token);
		}
		
		url = tm.getToken();
		
		if (url == null || url.trim().isEmpty()) {
			throw new SyntaxException("Missing url value.");
		}
		
		//handle variable
		if (url.equals("$")) {
			url = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, url);
		}
		
		//get optional timeout;
		
		int timeout = getTimeout(10);
		
		try {
			browserCommand.navigate(url, timeout);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		} catch (TimeoutCommandException e) {
			ret = -1;
		}
		
		return ret;
	}
	
	private void executeRefresh() {
		try {
			browserCommand.refresh();
		} catch (BrowserCommandException e) {
			LOG.info(e.getMessage());
		}
	}
	
	private void executeSelect() throws SyntaxException {
		String locator;
		String locatorValue;
		String option;
		String optionValue;
		String eq;

		locator = tm.getToken();
		
		if (locator != null &&
				!locator.equalsIgnoreCase("id") &&
				!locator.equalsIgnoreCase("xpath") &&
				!locator.equalsIgnoreCase("name")) {
				throw new SyntaxException("Unknown token: " + locator);
		}
		
		eq = tm.getToken();
		
		if (eq == null || !eq.equalsIgnoreCase("=")) {
			throw new SyntaxException("Expects = after " + locator);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null && locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		option = tm.getToken();
		
		if (option == null || 
				(!option.equalsIgnoreCase("name") &&
				 !option.equalsIgnoreCase("index"))) {		
			throw new SyntaxException("Expects name or index");
		}
		
		eq = tm.getToken();
		
		if (eq == null || !eq.equalsIgnoreCase("=")) {
			throw new SyntaxException("Expects = after " + locator);
		}
		
		optionValue = tm.getToken();
		
		if (optionValue.equals("$")) {
			optionValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, optionValue);
		}
		
		String[] optionValues = optionValue.split(",");
		
		int delay = getTimeout(1);
		
		try {
			browserCommand.select(locator, locatorValue, option, optionValues, delay);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
	}
	
	private void executeDeselect() throws SyntaxException {
		String locator;
		String locatorValue;
		String option;
		String optionValue;
		String eq;
		
		locator = tm.getToken();
		
		if (locator != null &&
				!locator.equalsIgnoreCase("id") &&
				!locator.equalsIgnoreCase("xpath") &&
				!locator.equalsIgnoreCase("name")) {
				throw new SyntaxException("Unknown token: " + locator);
		}
		
		eq = tm.getToken();
		
		if (eq == null || !eq.equalsIgnoreCase("=")) {
			throw new SyntaxException("Expects = after " + locator);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null && locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		option = tm.getToken();
		
		if (option == null || 
				(!option.equalsIgnoreCase("name") &&
				 !option.equalsIgnoreCase("index"))) {		
			throw new SyntaxException("Expects name or index");
		}
		
		eq = tm.getToken();
		
		if (eq == null || !eq.equalsIgnoreCase("=")) {
			throw new SyntaxException("Expects = after " + locator);
		}
		
		optionValue = tm.getToken();
		
		if (optionValue.equals("$")) {
			optionValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, optionValue);
		}
		
		String[] optionValues = optionValue.split(",");
		
		try {
			browserCommand.deselect(locator, locatorValue, option, optionValues);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
	}	
	
	private void executeQuit() throws SyntaxException{
		browserCommand.quit();
	}
	
	private int executeClick() throws SyntaxException {
		String locator;
		String token;
		String value;
		int ret = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		value = tm.getToken();
		
		if (value != null && value.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (value.equals("$")) {
			value = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, value);
		}
		
		int timeout = getTimeout(10);
		
		try {
			browserCommand.click(locator, value, timeout);
			ret = CommonConstants.PASSED;
		} catch (BrowserCommandException e) {
			ret = CommonConstants.FAILED;
			LOG.severe(e.getMessage());
		} catch (TimeoutCommandException e) {
			ret = CommonConstants.TIME_OUT;
		} 
		
		return ret;
	}
	
	private int executeDoubleClick() throws SyntaxException {
		String locator;
		String token;
		String value;
		int ret = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		value = tm.getToken();
		
		if (value != null && value.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (value.equals("$")) {
			value = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, value);
		}
		
		int timeout = getTimeout(10);
		
		try {
			browserCommand.doubleClick(locator, value, timeout);
			ret = CommonConstants.PASSED;
		} catch (BrowserCommandException e) {
			ret = CommonConstants.FAILED;
			System.out.println(e.getMessage());
		} catch (TimeoutCommandException e) {
			ret = CommonConstants.TIME_OUT;
		}
		
		return ret;
	}
	
	private void executeInput() throws SyntaxException {
		String locator;
		String token;
		String locatorValue;
		String value;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		if (locatorValue != null && locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing locator value.");
		}
		
		token = tm.getToken();
		if (token != null && !token.equalsIgnoreCase("value")) {
			throw new SyntaxException("unknown token: " + token + " expects value.");
		}
		
		token = tm.getToken();
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		value = tm.getToken();
		
		if (value != null && value.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (value.equals("$")) {
			value = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, value);
		}
		
		try {
			browserCommand.input(locator, locatorValue, value);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
		
	}
	
	private int executeVerify() throws SyntaxException {
		
		String locator;
		String locatorValue;
		String condition;
		String token;
		int ret = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null &&
			locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		token = tm.getToken();
		if (token != null &&
			!token.equalsIgnoreCase("value")) {
			throw new SyntaxException("unknown token: " + token + " expects value.");
		}
		
		condition = tm.getToken();
		
		if (condition != null &&
			!condition.equals("=") &&
			!condition.equals(">") &&
			!condition.equals("<") ) {
			throw new SyntaxException("Expects = or > or < near: " + token);
		}
		
		String valueToVerify = tm.getToken();
		
		//handle variable
		if (valueToVerify.equals("$")) {
			valueToVerify = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, valueToVerify);
		}
		
		try {
			boolean result = browserCommand.verify(locator, locatorValue, condition, valueToVerify);
			setActualResult(browserCommand.getText());
			if (result == true) {
				setStatus(true);
				LOG.info("Passed!");
				ret = CommonConstants.PASSED;
			} else {
				setStatus(false);
				LOG.info("Failed!");
				ret = CommonConstants.FAILED;
			}
		} catch (BrowserCommandException e) {
			LOG.info(e.getMessage());
			setStatus(false);
			ret = CommonConstants.FAILED;
		}
		
		return ret;
	}
	
	private void executeClear() throws SyntaxException {
		String locator;
		String locatorValue;
		String token;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null &&
			locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		try {
			browserCommand.clear(locator, locatorValue);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
			
		}
	}
	
	private void executePause() throws SyntaxException {
		String time;
		String token;
		
		token = tm.getToken();
		if (token != null && !token.equalsIgnoreCase("time")) {
			throw new SyntaxException("Unknown token: " + token + " expects [time]");
		}
		
		token = tm.getToken();
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		time = tm.getToken();
		if (time != null &&
				time.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (time.equals("$")) {
			time = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, time);
		}

		try {
			browserCommand.pause(Long.parseLong(time));
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
	}
	
	private int executePrintScreen() throws SyntaxException{

		tm.getToken();
		tm.getToken();
		int itemNo = Integer.parseInt(tm.getToken());
		
		try {
			browserCommand.printScreen(itemNo);
		} catch (BrowserCommandException e) {
			LOG.severe(e.getMessage());
		}
		

		return CommonConstants.PASSED;
	}
	
	private int executeWaitElementVisible() throws SyntaxException {
		String locator;
		String locatorValue;
		String token;
		int result = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null &&
			locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}

		int timeout = getTimeout(60);
		
		try {
			browserCommand.waitVisible(locator, locatorValue, timeout);
			result = CommonConstants.PASSED;
		} catch (BrowserCommandException e) {
			result = CommonConstants.FAILED;
			LOG.severe(e.getMessage());
		} catch (TimeoutCommandException e) {
			result = CommonConstants.TIME_OUT;
		}
		
		return result;
	}	
	
	private int executeWaitElementClickable() throws SyntaxException {
		String locator;
		String locatorValue;
		String token;
		int result = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null &&
			locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}
		
		int timeout = getTimeout(60);
		
		try {
			browserCommand.waitClickable(locator, locatorValue, timeout);
			result = CommonConstants.PASSED;
		} catch (BrowserCommandException e) {
			result = CommonConstants.FAILED;
			LOG.severe(e.getMessage());
		} catch (TimeoutCommandException e) {
			result = CommonConstants.TIME_OUT;
		}
		
		return result;
	}		
	
	private int executeTestElement(String command) throws SyntaxException {
		String locator;
		String locatorValue;
		String condition;
		String token;
		int ret = CommonConstants.FAILED;
		
		locator = tm.getToken();
		if (locator != null &&
			!locator.equalsIgnoreCase("id") &&
			!locator.equalsIgnoreCase("xpath") &&
			!locator.equalsIgnoreCase("class") &&
			!locator.equalsIgnoreCase("name")) {
			throw new SyntaxException("Unknown token: " + locator);
		}
		
		token = tm.getToken();
		
		if (token != null && !token.equals("=")) {
			throw new SyntaxException("Expects = near: " + token);
		}
		
		locatorValue = tm.getToken();
		
		if (locatorValue != null &&
			locatorValue.trim().isEmpty()) {
			throw new SyntaxException("Missing value.");
		}
		
		//handle variable
		if (locatorValue.equals("$")) {
			locatorValue = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, locatorValue);
		}		
		
		token = tm.getToken();
		if (token != null &&
			!token.equalsIgnoreCase("value")) {
			throw new SyntaxException("unknown token: " + token + " expects value.");
		}
		
		condition = tm.getToken();
		
		if (condition != null &&
			!condition.equals("=") &&
			!condition.equals(">") &&
			!condition.equals("<") ) {
			throw new SyntaxException("Expects = or > or < near: " + token);
		}
		
		String valueToVerify = tm.getToken();

		//handle variable
		if (valueToVerify.equals("$")) {
			valueToVerify = getVariable(CommonConstants.RESOURCE_BUNDLE, tm, valueToVerify);
		}		
		
		try {
			boolean result = false; 
			
			if (command.equals(TokenType.IS_ELEMENT_ENABLED.string())) {
				result = browserCommand.isElementEnabled(locator, locatorValue, condition, valueToVerify);
			} else if (command.equals(TokenType.IS_ELEMENT_SELECTED.string())) {
				result = browserCommand.isElementSelected(locator, locatorValue, condition, valueToVerify);
			} else if (command.equals(TokenType.IS_ELEMENT_VISIBLE.string())) {
				result = browserCommand.isElementVisible(locator, locatorValue, condition, valueToVerify);
			}
			
			setActualResult(browserCommand.getText());
			
			if (result == true) {
				setStatus(true);
				LOG.info("Passed!");
				ret = CommonConstants.PASSED;
			} else {
				setStatus(false);
				LOG.info("Failed!");
				ret = CommonConstants.FAILED;
			}
		} catch (BrowserCommandException e) {
			ret = CommonConstants.FALSE;
			LOG.severe(e.getMessage());
		}
		
		return ret;
	}
	
	private void executeLoad() throws SyntaxException{
		String nextToken = null;
		
		nextToken = tm.getToken();
		
		if (nextToken != null && 
			!nextToken.equalsIgnoreCase("data")) {
			throw new SyntaxException("Unknown token expects [data]");
		}
		
		nextToken = tm.getToken();
		
		if (nextToken != null &&
			!nextToken.equalsIgnoreCase("=")) {
			throw new SyntaxException("Unknown token expects [=]");
		}
		
		nextToken = tm.getToken();
		
		if (nextToken == null) {
			throw new SyntaxException("Missing value");
		}
		LOG.info(nextToken + " is loaded in CATYA");
		if (resourceBundle != null) {
			ResourceBundle.clearCache();
		} 
		resourceBundle = ResourceBundle.getBundle(nextToken);
	}
	
	private int executeIf() throws SyntaxException {
		int result = CommonConstants.FAILED;
		
		String variable = tm.getToken();
		
		if (variable == null || !variable.equals("$")) {
			throw new SyntaxException("missing token or token is not a variable");
		}
		
		//handle variable
		if (variable.equals("$")) {
			variable = getVariable(CommonConstants.GLOBAL_VARIABLES, tm, variable);
		}
		
		String condition = tm.getToken();
		
		if (condition == null || !condition.equalsIgnoreCase("is")) {
			throw new SyntaxException ("Expected \"is\" after a variable.");
		}
		
		condition = tm.getToken();
		if (condition == null || 
			(!condition.equalsIgnoreCase("Passed")  && 
			 !condition.equalsIgnoreCase("Failed")  &&
			 !condition.equalsIgnoreCase("Timeout")
			)
		) {
			throw new SyntaxException ("Expected \"passed | failed | timeout\"");
		}
		
		LOG.info("if " + variable + " is " + condition);
		if (!variable.equalsIgnoreCase(condition)) {
			result = CommonConstants.FAILED;
		} else {
			result = CommonConstants.PASSED;
		}
		LOG.info("result: " + result);
		return result;
	}
	
	private void executeFi() {
		
	}
	
	private void executeLoop(Integer ip) throws SyntaxException {
		String iteration = tm.getToken();
		
		Integer intIter = 1;
		try {
			intIter = Integer.parseInt(iteration);
		} catch (NumberFormatException e) {
			throw new SyntaxException("Expected Numeric Format after loop keyword");
		}
		
		String unit = tm.getToken();
		
		if (unit == null || !unit.equalsIgnoreCase("times")) {
			throw new SyntaxException("expected keyword \"times\" after loop number.");
		}
		
		LoopData data = new LoopData();
		data.setAddress(ip);
		data.setLoop(intIter);
		LOG.info("[executeLoop] - loopAddress: " + ip);
		LOG.info("[executeLoop] - loopCount  : " + intIter);
		stack.push(data);
	}
	
	private int executePool(Integer ip) {
		int ret = ip;
		LOG.info("[executePool]: loop stack is empty: " + stack.isEmpty());
		if (!stack.isEmpty()) {
			LoopData data = stack.pop();
			LOG.info("[pool: ]loopCount: " + data.getLoop());
			if (data.getLoop() > 1) {
				ret = data.getAddress();
				data.decrementLoop();
				stack.push(data);
				LOG.info("loopAddress: " + ret);
				LOG.info("decrement loopCount: " + data.getLoop());
			}
		}
		return ret;
	}
	
	private void executeExit() throws SyntaxException {
		String loop = tm.getToken();
		
		if (loop == null || !loop.equalsIgnoreCase("loop")) {
			throw new SyntaxException("Expects keyword \"loop\" after exit");
		}
		
		if (!stack.isEmpty()) {
			LoopData data = stack.pop();
			LOG.info("[exit loop]loopCount: " + data.getLoop());
			if (data.getLoop() > 1) {
				data.setLoop(1);
			}
			stack.push(data);
		}
	}
	
	public void setStatus(boolean executeStatus) {
		this.executeStatus = executeStatus;
	}
	
	public boolean getStatus() {
		return executeStatus;
	}
	
	@Override
	public void putGlobalVariable(String variableName, String value) {
		LOG.info("Put Global Variable: " + variableName + " = " + value);
		globalVariables.put(variableName, value);
	}
	
	public String getVariable(String storage, TokenManager tm, String text) throws SyntaxException {
		String value = null;

		String variable = tm.getToken();
		
		if (variable == null) {
			throw new SyntaxException("variable not found");
		}
		
		if (storage.equals(CommonConstants.RESOURCE_BUNDLE)) {
			//lookup property file
			if (resourceBundle == null) {
				throw new SyntaxException("Test Data definition not loaded");
			}
			
			value = resourceBundle.getString(variable);
			LOG.info("data: " + value);
		} else {
			value = globalVariables.get(variable);
			LOG.info("Global variable [" + variable + "]: " + value);
		}
		
		if (value == null) {
			throw new SyntaxException("variable not defined!");
		}
		
		return value;
	}
	
	public int getTimeout(int defaultTimeout) throws SyntaxException {
		String timeoutKey;
		String timeoutValue;
		int nTimeout;
		
		timeoutKey = tm.getToken();
		
		if (timeoutKey != null) {
			String assign = tm.getToken();
			
			if (assign == null || !assign.equals("=")) {
				throw new SyntaxException("Expects = after timeout keyword.");
			}
			
			timeoutValue = tm.getToken();
			
			if (timeoutValue == null || !timeoutValue.matches("[0-9]+")) {
				throw new SyntaxException("Expects number after = symbol.");
			}
			
			nTimeout = Integer.parseInt(timeoutValue);
		} else {
			nTimeout = defaultTimeout;
		}
		
		return nTimeout;
	}
	
	public void clearVariables() {
		globalVariables.clear();
	}
	
	public String getActualResult() {
		return actualResult;
	}
	
	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}
}
