/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.Message;

public class SyncMessage extends LockObject{

	String command;
	String response;
	
	public SyncMessage(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getResponse() {
		waitObject();
		return response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
}
