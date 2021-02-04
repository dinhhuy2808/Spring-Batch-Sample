package com.batch.model;

import org.apache.poi.xssf.usermodel.XSSFSheet;


public class DictionaryUploadProcessorInput extends ProcessorInput {
	private XSSFSheet sheet;
	public XSSFSheet getSheet() {
		return sheet;
	}
	public void setSheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}
}
