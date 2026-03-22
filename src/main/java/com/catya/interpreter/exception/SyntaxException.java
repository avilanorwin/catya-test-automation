/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.exception;

public class SyntaxException extends CatyaException{

	private static final long serialVersionUID = 1L;
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String message, ExceptionCause cause) {
		super(message, cause);
	}
}
