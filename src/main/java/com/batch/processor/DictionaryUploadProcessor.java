package com.batch.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.batch.item.ItemProcessor;

import com.batch.model.Dictionary;
import com.batch.model.DictionaryUploadProcessorInput;
import com.batch.model.DictionaryUploadProcessorOutput;
import com.batch.model.ProcessorInput;
import com.batch.model.ProcessorOutput;

public class DictionaryUploadProcessor implements ItemProcessor<ProcessorInput, ProcessorOutput> {
	
    public DictionaryUploadProcessorOutput process(ProcessorInput processorInput) {
    	DictionaryUploadProcessorInput dictionaryUploadProcessorInput = (DictionaryUploadProcessorInput) processorInput;
    	Row row = null;
    	XSSFSheet sheet = dictionaryUploadProcessorInput.getSheet();
		List<Dictionary> dics = new ArrayList<Dictionary>();
		int lastRow = sheet.getLastRowNum();
		for (int i = 1; i <= lastRow; i++) {
			row = sheet.getRow(i);
			if (row != null && row.getCell(1) != null && !row.getCell(1).toString().trim().equals("")) {
				Dictionary dic = new Dictionary();
				dic.setHantu(
						row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue().replace("\'", "\\'"));
				dic.setPinyin(
						row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue().replace("\'", "\\'"));
				dic.setNghia1(
						row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue().replace("\'", "\\'"));
				dic.setHanviet(row.getCell(4) == null ? ""
						: row.getCell(4).getStringCellValue().replace("...", "").replace("\'", "\\'").replace("…",
								""));

				dics.add(dic);
			}

		}
    	
    	DictionaryUploadProcessorOutput output = new DictionaryUploadProcessorOutput();
    	output.setDictionaries(dics);
    	return output;
    }
}
