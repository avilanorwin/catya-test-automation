/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.tokenizer;

public enum TokenType {
	CALL("call"),
	CLEAR("clear"),
	CLICK("click"),
	CLOSE("close"),
	DELIMITER("delimiter"),
	DESELECT("deselect"),
	DOUBLECLICK("doubleclick"),
	EXIT("exit"),
	FI("fi"),
	IF("if"),
	INPUT("input"),
	IS_ELEMENT_ENABLED("isElementEnabled"),
	IS_ELEMENT_SELECTED("isElementSelected"),
	IS_ELEMENT_VISIBLE("isElementVisible"),
	LOAD("load"),
	LOOP("loop"),
	NAVIGATE("navigate"),
	OPEN("open"),
	OTHER("other"),
	PAUSE("pause"),
	POOL("pool"),
	PRINTSCREEN("printscreen"),
	QUIT("quit"),
	QUOTED("quoted"),
	REFRESH("refresh"),
	SELECT("select"),
	VARIABLE("variable"),
	VERIFY("verify"),
	WAIT_CLICKABLE("waitElementClickable"),
	WAIT_VISIBLE("waitElementVisible");
	
	String tokenString;
	
	TokenType(String token) {
		tokenString = token;
	}
	
	public String string() {
		return tokenString;
	}
}
