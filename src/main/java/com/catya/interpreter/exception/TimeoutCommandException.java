/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.exception;

public class TimeoutCommandException extends CatyaException{
private static final long serialVersionUID = 1L;
	
	public TimeoutCommandException(String message) {
		super(message);
	}
	
	public TimeoutCommandException(String message, ExceptionCause cause) {
		super(message, cause);
	}
}
