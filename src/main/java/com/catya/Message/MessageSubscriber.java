/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.Message;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class MessageSubscriber implements Observer{

	protected volatile boolean isPaused = false;
	protected volatile boolean isStopped = false;
	protected volatile boolean isExecuted = false;
	protected LockObject lockObject;
	protected SyncMessage syncMessage = null;
	
	@Override
	public void update(Observable o, Object arg) {
		String command = null;
		
		if (arg instanceof String ) {
			command = (String)arg;
		} else if (arg instanceof SyncMessage) {
			syncMessage = (SyncMessage)arg;
			command = syncMessage.getCommand();
		}
		
		if (command.equals(MessageConstants.PAUSED)) {
			if (isExecuted)
				setPaused(true);
		} else if (command.equals(MessageConstants.STOPPED)) {
			setStopped(true);
			lockObject.notifyObject();
		} else if (command.equals(MessageConstants.EXECUTE)) {
			setPaused(false);
			setStopped(false);
			lockObject.notifyObject();
		}
	}
	
	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}

	public Object getLockObject() {
		return lockObject;
	}

	public void setLockObject(LockObject lockObject) {
		this.lockObject = lockObject;
	}
	
}
