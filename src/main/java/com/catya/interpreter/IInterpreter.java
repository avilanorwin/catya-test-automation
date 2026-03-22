/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter;

import com.catya.interpreter.exception.SyntaxException;
import com.catya.interpreter.exception.TimeoutCommandException;
import com.catya.interpreter.tokenizer.TokenManager;

public interface IInterpreter {
	public int interpretCommand(String command, Integer ip) throws SyntaxException, TimeoutCommandException;
	public void putGlobalVariable(String variableName, String value);
	public String getVariable(String storage, TokenManager tm, String text) throws SyntaxException;
	public boolean getStatus();
	public String getActualResult();
	public void setStatus(boolean status);
	public void clearVariables();
}
