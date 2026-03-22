/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.model.testscenario;

import java.awt.Color;
import java.util.HashMap;
import java.util.logging.Logger;
import com.catya.interpreter.IInterpreter;
import com.catya.interpreter.constants.CommonConstants;
import com.catya.interpreter.exception.ExecuteStoppedException;
import com.catya.interpreter.exception.SyntaxException;
import com.catya.interpreter.exception.TimeoutCommandException;
import com.catya.interpreter.tokenizer.TokenManager;
import com.catya.interpreter.tokenizer.TokenType;
import com.catya.ui.model.CATYAModel;

public class Steps {
	private CATYAModel model;
	private int rowNo;
	private IInterpreter interpreter;
	private HashMap<String, TestCase> testScenario;
	private boolean withStatus = false;
	private String name;
	
	private static Logger LOG = Logger.getLogger(Steps.class.getName());
	
	public Steps(HashMap<String, TestCase>scenario, int rowNo) {
		this.testScenario = scenario;
		this.rowNo = rowNo;
	}
	
	public int execute(Integer ip) throws SyntaxException, TimeoutCommandException, ExecuteStoppedException {
		String nextToken = null;
		int ret = CommonConstants.PASSED;
		boolean withAssignment = false;
		String variableName = null;
		
		String script = model.getCellValue(rowNo, 2);
		LOG.info("Executing script: " + script);
		
		TokenManager tokenManager = new TokenManager(script);
		nextToken = tokenManager.getToken();
		
		if (nextToken != null && tokenManager.getTokenType() == TokenType.DELIMITER) {
			if (nextToken.equals("$")) {
				withAssignment = true;
				variableName = tokenManager.getToken();
				nextToken = tokenManager.getToken();
				nextToken = tokenManager.getToken();
			}
		}
		
		setName(nextToken);
		
		if (nextToken != null && tokenManager.getTokenType() == TokenType.CALL) {
			// Execute a scenario
			nextToken = tokenManager.getToken();
			if (nextToken != null) {
				TestCase scenario = testScenario.get(nextToken);
				if (scenario == null) {
					LOG.info("Scenario: " + nextToken + " not found.");
				} else {
					model.setRowColor(rowNo, Color.ORANGE);
					try {
						scenario.execute();
					} finally {
						model.setRowColor(rowNo, Color.WHITE);
					}
					LOG.info("calling a scenario: " + nextToken);
				}
			}
		} else {
			if (tokenManager.getTokenType() == TokenType.VERIFY ||
				tokenManager.getTokenType() == TokenType.IS_ELEMENT_ENABLED ||
				tokenManager.getTokenType() == TokenType.IS_ELEMENT_SELECTED ||
				tokenManager.getTokenType() == TokenType.IS_ELEMENT_VISIBLE ) {
				
				script += " value = \"" + model.getCellValue(rowNo, 3).trim() + "\"";
			} else if (tokenManager.getTokenType() == TokenType.PRINTSCREEN) {
				script += " itemNo = \"" + rowNo + "\"";
			}
			
			ret = interpreter.interpretCommand(script, ip);
			
			if (withAssignment) {
				switch (ret) {
				case CommonConstants.TIME_OUT:
					interpreter.putGlobalVariable(variableName, TestResult.TIMEOUT);
					break;
				case CommonConstants.PASSED:
					interpreter.putGlobalVariable(variableName, TestResult.PASSED);
					break;
				case CommonConstants.FAILED:
					interpreter.putGlobalVariable(variableName, TestResult.FAILED);
					break;
				}
			}
			
			TokenType type = tokenManager.getTokenType();
			
			if (type == TokenType.VERIFY ||
				type == TokenType.IS_ELEMENT_ENABLED ||
				type == TokenType.IS_ELEMENT_SELECTED ||
				type == TokenType.IS_ELEMENT_VISIBLE) {
				withStatus = true;
				String actualResult = interpreter.getActualResult();
				model.setCellValue(rowNo, 4, actualResult);
			}
		}
		
		return ret;
	}
	
	public void setStatusColor() {
		if (interpreter.getStatus()) {
			model.setCellValue(rowNo, 5, TestResult.PASSED);
			model.setCellColor(rowNo, 5, Color.GREEN);
		} else {
			model.setCellValue(rowNo, 5, TestResult.FAILED);
			model.setCellColor(rowNo, 5, Color.RED);
		}
	}
	
	public boolean isWithStatus() {
		return withStatus;
	}

	public void setWithStatus(boolean withStatus) {
		this.withStatus = withStatus;
	}

	public IInterpreter getInterpreter() {
		return interpreter;
	}
	public void setInterpreter(IInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public CATYAModel getModel() {
		return model;
	}

	public void setModel(CATYAModel model) {
		this.model = model;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
