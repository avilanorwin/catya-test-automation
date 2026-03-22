/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.exception;

public class BrowserCommandException extends CatyaException{

	private static final long serialVersionUID = 1L;
	
	public BrowserCommandException(String message, ExceptionCause cause) {
		super(message, cause);
	}

}
