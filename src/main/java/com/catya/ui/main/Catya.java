/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.main;

import com.catya.ui.controller.CATYAController;
import com.catya.ui.model.CATYAModel;
import com.catya.ui.view.screens.CATYAScreen;

public class Catya {
	public static void main(String args[]) throws Exception {		
		CATYAController controller = new CATYAController(
											new CATYAScreen(
													new CATYAModel()));
		controller.start();		
	}
}
