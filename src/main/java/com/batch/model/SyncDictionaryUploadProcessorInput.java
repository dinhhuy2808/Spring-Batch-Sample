package com.batch.model;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public class SyncDictionaryUploadProcessorInput extends ProcessorInput {
	private XSSFSheet sheet1;
	private XSSFSheet sheet2;
	public XSSFSheet getSheet1() {
		return sheet1;
	}
	public void setSheet1(XSSFSheet sheet1) {
		this.sheet1 = sheet1;
	}
	public XSSFSheet getSheet2() {
		return sheet2;
	}
	public void setSheet2(XSSFSheet sheet2) {
		this.sheet2 = sheet2;
	}
}
