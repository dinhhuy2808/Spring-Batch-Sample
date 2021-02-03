package com.batch.model;

import org.apache.poi.ss.usermodel.Sheet;


public class ExerciseUploadProcessorInput extends ProcessorInput {
	private Sheet sheet;
	private Integer hsk;
	public Sheet getSheet() {
		return sheet;
	}
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
}
