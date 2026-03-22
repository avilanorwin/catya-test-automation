/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.view.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FWTableCellRenderer extends DefaultTableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		FWTableModel model = (FWTableModel) table.getModel();
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setBackground(model.getCellColor(row, column));
		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
			c.setForeground(table.getSelectionForeground());
		} else {
			c.setForeground(Color.BLACK);	
		}
		return c;
	}

}
