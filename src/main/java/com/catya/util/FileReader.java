/**
 * @author Eldon Leuterio
 * @author Norwin Avila - Added excel formatting
 * Copyright (c) 2017
 * 
 */
package com.catya.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catya.ui.model.CATYAModel;
import com.catya.ui.model.testscenario.TestResult;

public class FileReader {
	
	private static Logger LOG = Logger.getLogger(FileReader.class.getName());

	public static void saveFile(CATYAModel model, String fileName) throws IOException{
		
		if(!fileName.toLowerCase().endsWith(".xlsx")){
			fileName += ".xlsx";
		}
		
		XSSFWorkbook workbook = null;
		workbook = new XSSFWorkbook();
		
		XSSFSheet sheet = workbook.createSheet("Sheet1");
		sheet.setDisplayGridlines(false);
		sheet.setColumnWidth(0, 800);
		sheet.setColumnWidth(2, 10000);
		sheet.setColumnWidth(3, 20000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		
		//add row margin
		sheet.createRow(0);
		
		//add row header
		XSSFRow rowHeader = sheet.createRow(1);
		
		//add column heading.
		XSSFCell itemCell = rowHeader.createCell(1);
		XSSFCell scenarioCell = rowHeader.createCell(2);
		XSSFCell stepCell = rowHeader.createCell(3);
		XSSFCell expectedCell = rowHeader.createCell(4);
		XSSFCell actualCell = rowHeader.createCell(5);
		XSSFCell resultCell = rowHeader.createCell(6);
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		
		itemCell.setCellValue("Item");
		itemCell.setCellStyle(headerStyle);
		scenarioCell.setCellValue("Test Cases");
		scenarioCell.setCellStyle(headerStyle);
		stepCell.setCellValue("Steps");
		stepCell.setCellStyle(headerStyle);
		expectedCell.setCellValue("Expected");
		expectedCell.setCellStyle(headerStyle);
		actualCell.setCellValue("Actual");
		actualCell.setCellStyle(headerStyle);
		resultCell.setCellValue("Result");
		resultCell.setCellStyle(headerStyle);
		
		//need for-loop for index
		int size = model.getActualNumRows();
		LOG.info("TableData size: " + size);
		for(int rownum=0; rownum<size; rownum++){
			XSSFRow row = sheet.createRow(rownum+2);
			
			int colSize = 6;
			for(int cellnum=0; cellnum<colSize; cellnum++){
				XSSFCell cell = row.createCell(cellnum+1);
				cell.setCellValue(model.getCellValue(rownum, cellnum));
				XSSFCellStyle style = workbook.createCellStyle();
				if (cellnum == 5) style.setBorderRight(BorderStyle.THIN);
				if (rownum >= size-1) style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				
				if (!model.getCellValue(rownum, 1).isEmpty()) {
					style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				} 
				
				if (cellnum == 5) {
					if (model.getCellValue(rownum, 5).equals(TestResult.PASSED)) {
						style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					} else if (model.getCellValue(rownum, 5).equals(TestResult.FAILED)) {
						style.setFillForegroundColor(IndexedColors.RED.getIndex());
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
				}
				cell.setCellStyle(style);
			}
		}
		
		FileReader.writeFile(workbook, fileName);
	}
	
	public static void loadFile(CATYAModel model, String filename, String sheetName) throws IOException{
		
		model.clear();
		
		try (FileInputStream fin = new FileInputStream(new File(filename));
			XSSFWorkbook workbook = new XSSFWorkbook(fin)) {
		
			XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));
			
			int totalRows = sheet.getPhysicalNumberOfRows()-1;
			
			LOG.info("Total Rows: " + totalRows);
			
			for(int rowNum=0; rowNum < totalRows; rowNum++){
				XSSFRow cellRow = sheet.getRow(rowNum+2);
				if(null != cellRow){
					int cellSize = cellRow.getLastCellNum()-1;
					for(int cellNum=0; cellNum < cellSize; cellNum++){
						
						XSSFCell cell = cellRow.getCell(cellNum+1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						
						String value = null;
						
						switch (cell.getCellType()) {
							case CellType.NUMERIC:
								value = String.valueOf((int) cell.getNumericCellValue());
								break;
							case CellType.STRING:
								value = cell.getStringCellValue();
								break;
							case CellType.BLANK:
								value = "";
								break;
							case CellType.FORMULA:
								value = cell.getCellFormula();
								break;
							default:
								break;
						}
						model.setCellValue(rowNum, cellNum, value);
					}
				}
			}
			model.updateNumRows();
			fin.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
	
	public static List<String> getSheets(String filename) throws IOException{
		List<String> sheets = new ArrayList<String>();
		
		try (FileInputStream fin = new FileInputStream(new File(filename));
			XSSFWorkbook workbook = new XSSFWorkbook(fin)) {
			Iterator<Sheet> itr = workbook.iterator();
			while(itr.hasNext()){
				XSSFSheet sheet = (XSSFSheet) itr.next();
				sheets.add(sheet.getSheetName());
			}
			
			fin.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		return sheets;
	}
	
	private static void writeFile(XSSFWorkbook workbook, String filename) throws IOException {
		FileOutputStream out = new FileOutputStream(filename);
	    try {
	    	workbook.write(out);
	    } finally {
	        out.close();
	    }
	}
}
