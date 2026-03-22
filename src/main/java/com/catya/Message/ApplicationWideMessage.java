/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */

package com.catya.Message;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class ApplicationWideMessage extends Observable {
	
	public void sendMessage(String message) {
		setChanged();
		notifyObservers(message);
	}
	
	public void sendSyncMessage(SyncMessage msg) {
		setChanged();
		notifyObservers(msg);
	}
}
