/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.screens;

import com.catya.Message.MessageConstants;

public class ScreenState {
	private CATYAScreen screen;
	private String state;
	
	public ScreenState(CATYAScreen screen) {
		this.screen = screen;
	}
	
	public void init() {
		state = MessageConstants.INIT;
		screen.getStartButton().setEnabled(false);
		screen.getPauseButton().setEnabled(false);
		screen.getStopButton().setEnabled(false);
		screen.getSaveFileMenuItem().setEnabled(false);
		screen.getCloseFileMenuItem().setEnabled(false);
	}
	
	public void stopping() {
		state = MessageConstants.STOPPING;
		screen.getFileMenu().setEnabled(false);
		screen.getStartButton().setEnabled(false);
		screen.getPauseButton().setEnabled(false);
		screen.getStopButton().setEnabled(false);
	}
	
	public void execute() {
		state = MessageConstants.EXECUTE;
		screen.getFileMenu().setEnabled(false);
		screen.getStartButton().setEnabled(false);
		screen.getStopButton().setEnabled(true);
		screen.getPauseButton().setEnabled(true);
	}
	
	public void pause() {
		state = MessageConstants.PAUSED;
		screen.getFileMenu().setEnabled(false);
		screen.getPauseButton().setEnabled(false);
		screen.getStartButton().setEnabled(true);
		screen.getStopButton().setEnabled(true);
	}
	
	public void stop() {
		state = MessageConstants.STOPPED;
		screen.getFileMenu().setEnabled(true);
		screen.getSaveFileMenuItem().setEnabled(true);
		screen.getCloseFileMenuItem().setEnabled(true);
		screen.getStopButton().setEnabled(false);
		screen.getStartButton().setEnabled(true);
		screen.getPauseButton().setEnabled(false);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
