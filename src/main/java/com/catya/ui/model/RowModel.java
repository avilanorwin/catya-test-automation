/**
 * @author Norwin T. Avila
 * Copyright (c) 2017
 * 
 */
package com.catya.ui.model;

import java.awt.Color;


public class RowModel {
	private String data;
	private int row;
	private int col;
	Color color;
	
	public RowModel(int row, int col, Color color) {
		this.data = "";
		this.row = row;
		this.col = col;
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public RowModel() {
		this.data = "";
	}
	
	public RowModel(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public void setCol(int col) {
		this.col= col;
	}
	
	@Override
	public String toString() {
		return data;
	}
}
