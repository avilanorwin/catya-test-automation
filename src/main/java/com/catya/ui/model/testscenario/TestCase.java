/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.model.testscenario;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.catya.Message.MessageSubscriber;
import com.catya.interpreter.IInterpreter;
import com.catya.interpreter.constants.CommonConstants;
import com.catya.interpreter.exception.ExecuteStoppedException;
import com.catya.interpreter.exception.SyntaxException;
import com.catya.interpreter.exception.TimeoutCommandException;
import com.catya.interpreter.tokenizer.TokenManager;
import com.catya.interpreter.tokenizer.TokenType;
import com.catya.ui.model.CATYAModel;

public class TestCase extends MessageSubscriber{
	private CATYAModel model;
	private int itemNo;
	private String name;
	private List<Steps> steps;
	private IInterpreter interpreter;
	private static Logger LOG = Logger.getLogger(TestCase.class.getName());
	
	public TestCase(CATYAModel model, IInterpreter interpreter) {
		this.setModel(model);
		steps = new ArrayList<>();
		this.setInterpreter(interpreter);
	}
	
	public void addSteps(Steps steps) {
		steps.setInterpreter(this.interpreter);
		steps.setModel(this.model);
		TokenManager tm = new TokenManager(model.getCellValue(steps.getRowNo(), 2));
		steps.setName(tm.getToken().toLowerCase());
		this.steps.add(steps);
	}
	
	public List<Steps> getSteps() {
		return steps;
	}

	public IInterpreter getInterpreter() {
		return interpreter;
	}

	public void setInterpreter(IInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void execute() throws SyntaxException, TimeoutCommandException, ExecuteStoppedException {
		setExecuted(true);
		Steps step = null;
		try {
			Integer instructionPointer = 0;
			while (instructionPointer < steps.size()) {
				step = steps.get(instructionPointer++);
				
				model.setRowColor(step.getRowNo(), Color.YELLOW);
				if (isPaused) {
					LOG.info("Executioh was paused by user!");
					lockObject.waitObject();
				} 
				
				if (isStopped) {
					model.setRowColor(step.getRowNo(), Color.WHITE);
					syncMessage.setResponse("Stopped by User!");
					syncMessage.notifyObject();
					throw new ExecuteStoppedException("ScenarioExecution was stopped by user");
				}
				try {
					int result = step.execute(instructionPointer);
					String stepName = step.getName();
					if (result == CommonConstants.FAILED && stepName.equalsIgnoreCase(TokenType.IF.string())) {
						int skipIf = 0;
						while (instructionPointer < steps.size()) {
							Steps skipStep = steps.get(instructionPointer++);
							String command = skipStep.getName();
							if (command.equalsIgnoreCase(TokenType.IF.string())) {
								skipIf++;
							} else if (command.equalsIgnoreCase(TokenType.FI.string()) && skipIf > 0) {
								skipIf--;
							} else {
								if (command.equalsIgnoreCase(TokenType.FI.string())) {
									break;
								}
							}
						}
					} else if (stepName.equalsIgnoreCase(TokenType.POOL.string())) {
						instructionPointer = result;
						LOG.info("Executing pool loop address: " + instructionPointer);
					} else if (stepName.equalsIgnoreCase(TokenType.EXIT.string())) {
						while (instructionPointer < steps.size()) {
							Steps skipStep = steps.get(instructionPointer++);
							if (skipStep.getName().equalsIgnoreCase(TokenType.POOL.string())) {
								instructionPointer--;
								break;
							}
						}
					}
				} catch (SyntaxException e) {
					if (step != null) {
						model.setRowColor(step.getRowNo(), Color.RED);
						model.setCellValue(step.getRowNo(), 4, e.getMessage());
						throw new SyntaxException(e.getMessage());
					}
				}
				
				model.setRowColor(step.getRowNo(), Color.WHITE);
				if (step.isWithStatus()) {
					step.setStatusColor();
				}
			}
		} finally {
			setExecuted(false);
		}
		
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public CATYAModel getModel() {
		return model;
	}

	public void setModel(CATYAModel model) {
		this.model = model;
	}
}
