/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.components;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.catya.ui.model.RowModel;
import com.catya.ui.model.testscenario.TestResult;

public class FWTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -2392453051184233060L;
	private String columnNames[];
	List<List<RowModel>> data = new ArrayList<List<RowModel>>();
	private String sheet;
	private int actualNumRows;
	private List<IFWTableModelListener> listeners = new ArrayList<>();
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}
	
	public String getCellValue(int row, int col) {
		return data.get(row).get(col).getData();
	}
	
	public void setCellValue(int row, int col, String value) {
		data.get(row).get(col).setData(value);
		fireTableCellUpdated(row, col);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
		RowModel row = (RowModel)getValueAt(0, column);
		return row.getData().getClass();
	}

	
	public void setValueAt(Object value, int row, int col){

		if (col == 1) {
			clearRow(row);
			if (value.toString().trim().isEmpty()) {
				setRowColor(row, Color.WHITE);
			} else { 
				setRowColor(row, Color.LIGHT_GRAY);
			}
		} else {
			setRowColor(row, Color.WHITE);
		}
		data.get(row).get(col).setData(value.toString());
		fireTableCellUpdated(row, col);
		String result = data.get(row).get(5).getData();
		if (!result.isEmpty()) {
			if (result.equalsIgnoreCase(TestResult.PASSED)) {
				setCellColor(row, 5, Color.GREEN);
			} else {
				setCellColor(row, 5, Color.RED);
			}
			fireTableCellUpdated(row, 5);
		}
		updateTestItemNumber();
	}

	public void setColumnNames(String columnNames[]) {
		this.columnNames = columnNames;
	}
	
	public boolean isCellEditable(int row, int col) {
		boolean ret;
		if (col < 4 && col != 0 && (data.get(row).get(1).toString().isEmpty() || col == 1)) {
			ret = true;
		} else {
			ret = false;
		}
		return ret;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void addRow(List<RowModel> rowData) {
		data.add(rowData);
		fireTableDataChanged();
	}
	
	public void clearTable() {
		data.clear();
		fireTableDataChanged();
	}
	
	public List<List<RowModel>> getTableData() {
		return data;
	}

	private void clearRow(int row) {
		for (RowModel col: data.get(row)) {
			col.setData("");
		}
	}
	
	public void setRowColor(int row, Color color) {
		for (RowModel col: data.get(row)) {
			//if (col.getData().trim().isEmpty())
			col.setColor(color);
		}
		fireTableRowsUpdated(row, row);
	}
	
	public void updateTestItemNumber() {
		int testCaseNo = 1;
		actualNumRows = 0;
		for (List<RowModel> rowList: data) {
			if (!rowList.get(1).getData().isEmpty()) {
				int row = rowList.get(1).getRow();
				data.get(row).get(0).setData(String.valueOf(testCaseNo));
				fireTableCellUpdated(row, 0);
				testCaseNo++;
			}
			
			if (!isRowEmpty(rowList)) {
				actualNumRows++;
			}
		}
		for (IFWTableModelListener listener: listeners) {
			listener.update(actualNumRows);
		}
	}
	
	public boolean isRowEmpty(List<RowModel> data) {
		return (data.get(0).getData().isEmpty() &&
				data.get(1).getData().isEmpty() &&
				data.get(2).getData().isEmpty() &&
				data.get(3).getData().isEmpty() &&
				data.get(4).getData().isEmpty() &&
				data.get(5).getData().isEmpty()
				);
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	
	public void clear(){
		data.clear();
	}
	
	public int getActualNumRows() {
		return actualNumRows;
	}
	
	public void addListener(IFWTableModelListener listener) {
		listeners.add(listener);
	}
	
	public Color getCellColor(int row, int col) {
		return data.get(row).get(col).getColor();
	}
	
	public void setCellColor(int row, int col, Color color) {
		data.get(row).get(col).setColor(color);
		fireTableCellUpdated(row, col);
	}
}
