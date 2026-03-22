package com.catya.interpreter;

public class LoopData {
	private Integer address;
	private Integer loop;
	public Integer getAddress() {
		return address;
	}
	public void setAddress(Integer address) {
		this.address = address;
	}
	public Integer getLoop() {
		return loop;
	}
	public void setLoop(Integer loop) {
		this.loop = loop;
	}
	
	public void decrementLoop() {
		loop--;
	}
}
