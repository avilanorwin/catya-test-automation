/**
 * @author Norwin T. Avila
 * @author Eldon Leuterio - Added business logic for Load and Save script file.
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.controller;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import com.catya.Message.ApplicationWideMessage;
import com.catya.Message.LockObject;
import com.catya.Message.MessageConstants;
import com.catya.Message.SyncMessage;
import com.catya.interpreter.DSLInterpreter;
import com.catya.interpreter.IInterpreter;
import com.catya.interpreter.constants.CommonConstants;
import com.catya.interpreter.exception.ExecuteStoppedException;
import com.catya.interpreter.exception.SyntaxException;
import com.catya.interpreter.exception.TimeoutCommandException;
import com.catya.interpreter.tokenizer.TokenType;
import com.catya.ui.config.ScreenConfig;
import com.catya.ui.model.CATYAModel;
import com.catya.ui.model.testscenario.Steps;
import com.catya.ui.model.testscenario.TestCase;
import com.catya.ui.view.components.IWaitDialogTask;
import com.catya.ui.view.components.WaitDialog;
import com.catya.ui.view.screens.CATYAScreen;
import com.catya.ui.view.screens.ScreenState;
import com.catya.util.FileReader;

public class CATYAController {
	private IInterpreter interpreter;
	private CATYAScreen catyaScreen;
	private ScreenState screenState;
	private HashMap<String, TestCase> scenarioList = new HashMap<>();
	private ApplicationWideMessage appMessage = new ApplicationWideMessage();
	private LockObject lockObject = new LockObject();
	private Logger LOG = Logger.getLogger(CATYAController.class.getName());
	private String lastDirectory;
	private Preferences prefs;
	
	public CATYAController(CATYAScreen catyaScreen) {
		this.catyaScreen = catyaScreen;
		this.catyaScreen.setController(this);
		this.screenState = new ScreenState(catyaScreen);
		this.interpreter = new DSLInterpreter();
		prefs = Preferences.userNodeForPackage(CATYAController.class);
		lastDirectory = prefs.get("lastDirectory", null);
		this.catyaScreen.getModel().addListener((numRows)-> {
			int actualNumRows = (int)numRows;
			if (actualNumRows == 0) {
				screenState.init();
			} else {
				if (screenState.getState().equals(MessageConstants.INIT)) {
					screenState.stop();
				}
			}
		});
	}
	
	public void start() {
		catyaScreen.initScreen();
		screenState.init();
	}
	
	public void execute() {
		if (screenState.getState().equals(MessageConstants.STOPPED)) {
			screenState.execute();
			
			interpreter.clearVariables();
			
			buildTestScenarios();
			
			catyaScreen.getTable().clearSelection();
			catyaScreen.getModel().clearResult();
			
			try {
				//------------------------------
				//
				//get START routine
				//
				//------------------------------
				TestCase main = scenarioList.get(CommonConstants.START);
				try {
					main.execute();
				} catch (SyntaxException e) {
					LOG.info(e.getMessage());
				} catch (TimeoutCommandException e) {
					LOG.info(e.getMessage());
				} catch (ExecuteStoppedException e) {
					LOG.info(e.getMessage());
				} 
			} finally {
				try {
					interpreter.interpretCommand(TokenType.QUIT.string(), 0);
				} catch (Exception e) {
					LOG.severe(e.getMessage());
				}
				screenState.stop();
			}
		} else {
			appMessage.sendMessage(MessageConstants.EXECUTE);
			screenState.execute();
		}
	}
	
	public void stopExecute() {
		screenState.stopping();
		
		WaitDialog waitDialog = new WaitDialog(catyaScreen, "Stopping execution");
		
		IWaitDialogTask task = ()-> {
			SyncMessage msg = new SyncMessage(MessageConstants.STOPPED);
			appMessage.sendSyncMessage(msg);
			String response = msg.getResponse();
			LOG.info("Stop execute response: " + response);
		};
		
		waitDialog.executeTask(task);
	}
	
	public void pauseExecute() {
		screenState.pause();
		appMessage.sendMessage(MessageConstants.PAUSED);
	}
	
	public void closeWindow() {
		int retConfirm = JOptionPane.showConfirmDialog(catyaScreen.getFrame(), 
				catyaScreen.getPropertyFileString(ScreenConfig.MSG_CONFIRMATION),
				catyaScreen.getPropertyFileString(ScreenConfig.WINDOW_TITLE),
				JOptionPane.YES_NO_OPTION);
		
		if (retConfirm == JOptionPane.YES_OPTION) {
			catyaScreen.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			catyaScreen.getFrame().dispose();
		}
	}
	
	private void buildTestScenarios() {
		String scenarioName = null;

		CATYAModel model = catyaScreen.getModel();
		int numRows = model.getActualNumRows();
		for (int row = 0; row < numRows; row++) {
			if (!model.getCellValue(row, 1).isEmpty()) {
				TestCase scenario = new TestCase(model, interpreter);
				scenario.setName(model.getCellValue(row, 1));
				scenarioName = scenario.getName();
				scenario.setLockObject(lockObject);
				appMessage.addObserver(scenario);
				scenarioList.put(scenarioName, scenario);
			} else if (!model.getCellValue(row, 2).isEmpty()) {	
				if (scenarioName != null)
					scenarioList.get(scenarioName).addSteps(new Steps(scenarioList, row));
			}
		}
	}
	public String getFile(String command){
		String absFilePath = null;
		//open fileChooser
		JFileChooser chooser = new JFileChooser();
		if (lastDirectory != null) {
			chooser.setCurrentDirectory(new File(lastDirectory));
		}
		FileFilter filter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Excel files (.xlsx)";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
			           return true;
			       } else {
			           String filename = f.getName().toLowerCase();
			           return filename.endsWith(".xlsx");
			       }
			}
		};
		chooser.setFileFilter(filter);
		
		int opt;
		
		if (command.equals(ScreenConfig.COMMAND_SAVE)) {
			opt = chooser.showSaveDialog(catyaScreen.getFrame());
		} else {
			opt = chooser.showOpenDialog(catyaScreen.getFrame());
		}
		
		if (JFileChooser.APPROVE_OPTION == opt) {
			absFilePath = chooser.getSelectedFile().getAbsoluteFile().getAbsolutePath();
			lastDirectory = chooser.getSelectedFile().getParent();
			prefs.put("lastDirectory", lastDirectory);
		}
		return absFilePath;
	}
	
	public void reset(){
		catyaScreen.getModel().clear();
	}
	
	public void loadFile(String file){
			if(null != file){
				
			WaitDialog waitDialog = new WaitDialog(catyaScreen, "Opening File");
			
			IWaitDialogTask task = ()->{
				String item ="Sheet1";
				try {
					catyaScreen.getModel().clear();
					FileReader.loadFile(catyaScreen.getModel(), file, item);
				} catch (IOException e) {
					LOG.severe(e.getMessage());
				}
			};
			
			waitDialog.executeTask(task);
			
			screenState.stop();
		}
	}
	
	public void saveFile(String file){
		if(null != file){
			
			WaitDialog waitDialog = new WaitDialog(catyaScreen, "Saving File");
			
			IWaitDialogTask task = ()->{
			
				try {
					FileReader.saveFile(catyaScreen.getModel(), file);
				} catch (IOException e) {
					LOG.severe(e.getMessage());
				}
			};
			
			waitDialog.executeTask(task);
			//alert
			JOptionPane.showMessageDialog(null, file+" saved.");
		}
	}
	
	public void closeFile() {
		
		int option = JOptionPane.showConfirmDialog(catyaScreen.getFrame(), "Are you sure you want to close the file?", "Catya", JOptionPane.YES_NO_OPTION);
		
		if (option == JOptionPane.YES_OPTION) {
		
			WaitDialog waitDialog = new WaitDialog(catyaScreen, "Closing File");
			
			IWaitDialogTask task = ()-> {
				catyaScreen.getModel().clear();
			};
			
			waitDialog.executeTask(task);
			screenState.init();
		}
	}
	
	public void about() {
		JOptionPane.showMessageDialog(catyaScreen.getFrame(), 
				"CATYA v1.0\nCopyright (c) 2017\n\nAuthor:\n\t\t\tNorwin T. Avila\n\t\t\tEldon Leuterio", 
				"About",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void quit() {
		catyaScreen.closeWindow();
	}
}
