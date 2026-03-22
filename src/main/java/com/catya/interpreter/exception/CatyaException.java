/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.interpreter.exception;

public class CatyaException extends Exception{

	private static final long serialVersionUID = 1L;
	private ExceptionCause cause;
	
	public CatyaException(String message) {
		super(message);
	}
	
	public CatyaException(String message, ExceptionCause cause) {
		this(message);
		this.cause = cause;
	}
	
	public ExceptionCause getExceptionCause() {
		return cause;
	}
}
