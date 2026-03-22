/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.exception;

public class ExecuteStoppedException extends CatyaException {
	private static final long serialVersionUID = 1L;
	
	public ExecuteStoppedException(String message) {
		super(message);
	}
	
	public ExecuteStoppedException(String message, ExceptionCause cause) {
		super(message, cause);
	}

}
