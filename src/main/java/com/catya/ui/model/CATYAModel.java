/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.catya.ui.model.testscenario.TestResult;
import com.catya.ui.view.components.FWTableModel;
import com.catya.ui.view.components.IFWTableModelListener;

@SuppressWarnings("deprecation")
public class CATYAModel extends Observable {

	private FWTableModel model;
	//private int actualNumRows;

	public void setModel(FWTableModel model) {
		this.model = model;
	}
	
	public void initModel(int numRows) {
		for (int j = 0; j < numRows; j++) {
			final List<RowModel> rowData = new ArrayList<RowModel>();
			RowModel item     = new RowModel(j, 0, Color.WHITE);
			RowModel scenario = new RowModel(j, 1, Color.WHITE);
			RowModel steps    = new RowModel(j, 2, Color.WHITE);
			RowModel expected = new RowModel(j, 3, Color.WHITE);
			RowModel actual   = new RowModel(j, 4, Color.WHITE);
			RowModel status   = new RowModel(j, 5, Color.WHITE);

			rowData.add(item);
			rowData.add(scenario);
			rowData.add(steps);
			rowData.add(expected);
			rowData.add(actual);
			rowData.add(status);
			model.addRow(rowData);
		}
	}
	
	public String getCellValue(int row, int col) {
		return model.getCellValue(row, col);
	}
	
	public void setCellValue(int row, int col, String data) {
		model.setCellValue(row, col, data);
		
		if (col == 1 && !data.isEmpty()) {
			setRowColor(row, Color.LIGHT_GRAY);
		} else if (col == 5) {
			if (data.equals(TestResult.PASSED)) {
				setCellColor(row, col, Color.GREEN);
			} else if (data.equals(TestResult.FAILED)) {
				setCellColor(row, col, Color.RED);
			}
		}
	}
	
	public Color getCellColor(int row, int col) {
		return model.getCellColor(row, col);
	}
	
	public void setCellColor(int row, int col, Color color) {
		model.setCellColor(row, col, color);
	}
	
	public void setRowColor(int row, Color color) {
		model.setRowColor(row, color);
	}
	
	public FWTableModel getTableModel() {
		return model;
	}	
	
	public void clear(){
		int actualNumRows = model.getActualNumRows();
		for (int row = 0; row < actualNumRows; row++) {
			setCellValue(row, 0, "");
			setCellValue(row, 1, "");
			setCellValue(row, 2, "");
			setCellValue(row, 3, "");
			setCellValue(row, 4, "");
			setCellValue(row, 5, "");
			setRowColor(row, Color.WHITE);
		}
	}
	
	public void clearResult() {
		
		int actualNumRows = model.getActualNumRows();
		
		for (int row = 0; row < actualNumRows; row++) {
			String status = getCellValue(row, 5);
			if (status != null && !status.isEmpty()) {
				setCellValue(row, 5, "");
				setCellColor(row, 5, Color.WHITE);
			}
			
			String result = getCellValue(row, 4);
			
			if (result != null && !result.isEmpty()) {
				setCellValue(row, 4, "");
			}
		}
	}

	public int getActualNumRows() {
		return model.getActualNumRows();
	}
	
	public void addListener(IFWTableModelListener listener) {
		model.addListener(listener);
	}
	
	public void updateNumRows() {
		model.updateTestItemNumber();
	}
}