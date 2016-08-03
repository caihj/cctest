package com.berbon.jfaccount.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.berbon.jfaccount.utils.MyUtils;
import com.berbon.mobilecharge.model.BatchOrderForUpdate;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

@Service
public class MobileBatchService {

	Logger logger = LoggerFactory.getLogger(MobileBatchService.class);
	final String OFFICE_EXCEL_2003_POSTFIX = "xls";
	final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";

	public List<BatchOrderForUpdate> readExcel(String path) {
		try {

			if (path == null || "".equals(path)) {
				return null;
			} else {
				String postfix = MyUtils.getPostfix(path);
				if (!"".equals(postfix)) {
					if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
						return readXls(path);
					} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
						return readXlsx(path);
					}
				} else {
					logger.info(path + ": Not the Excel file!");
				}
			}

			return null;

		} catch (Exception e) {
			logger.info("", e);
			return null;
		}
	}

	/**
	 * Read the Excel 2010
	 * 
	 * @param path
	 *            the path of the excel file
	 * @return
	 * @throws IOException
	 */
	public List<BatchOrderForUpdate> readXlsx(String path) throws IOException {
		InputStream is = new FileInputStream(path);
		@SuppressWarnings("resource")
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		BatchOrderForUpdate batchOrder = null;
		List<BatchOrderForUpdate> list = new ArrayList<BatchOrderForUpdate>();
		// Read the Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}
			// Read the Row
			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow != null) {
					batchOrder = new BatchOrderForUpdate();
					XSSFCell mob = xssfRow.getCell(0);
					mob.setCellType(1);
					XSSFCell amount = xssfRow.getCell(1);
					batchOrder.setMob(getValue(mob));
					batchOrder.setAmount(strToInt(getValue(amount)));
					list.add(batchOrder);
				}
			}
		}
		return list;
	}

	/**
	 * Read the Excel 2003-2007
	 * 
	 * @param path
	 *            the path of the Excel
	 * @return
	 * @throws IOException
	 */
	public List<BatchOrderForUpdate> readXls(String path) throws IOException {
		InputStream is = new FileInputStream(path);
		@SuppressWarnings("resource")
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		BatchOrderForUpdate batchOrder = null;
		List<BatchOrderForUpdate> list = new ArrayList<BatchOrderForUpdate>();
		// Read the Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}
			// Read the Row
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow != null) {
					batchOrder = new BatchOrderForUpdate();
					HSSFCell mob = hssfRow.getCell(0);
					mob.setCellType(1);
					HSSFCell amount = hssfRow.getCell(1);
					batchOrder.setMob(getValue(mob));
					batchOrder.setAmount(strToInt(getValue(amount)));
					list.add(batchOrder);
				}
			}
		}
		return list;
	}
	
	

	@SuppressWarnings("static-access")
	private String getValue(XSSFCell xssfRow) {
		if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfRow.getBooleanCellValue());
		} else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
			return String.valueOf(xssfRow.getNumericCellValue());
		} else {
			return String.valueOf(xssfRow.getStringCellValue());
		}
	}

	@SuppressWarnings("static-access")
	private String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	private int strToInt(String value) {
		if (value.contains(".")) {
			return Integer.parseInt(value.split("\\.")[0]) * 100;
		} else {
			return Integer.parseInt(value) * 100;
		}

	}
	
	
	
	

}
