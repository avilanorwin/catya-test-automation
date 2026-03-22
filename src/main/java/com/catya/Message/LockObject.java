/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.Message;

public class LockObject {
	public synchronized void waitObject() {
		try {
			wait();
		} catch (InterruptedException e) {
			System.out.println("Wait interrupted!");
		}
	}
	
	public synchronized void notifyObject() {
		notifyAll();
	}
}
