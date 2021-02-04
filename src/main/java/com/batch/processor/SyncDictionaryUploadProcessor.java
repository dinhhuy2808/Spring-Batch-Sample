package com.batch.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.batch.model.DictionaryUploadProcessorInput;
import com.batch.model.LessonDictionary;
import com.batch.model.ProcessorInput;
import com.batch.model.ProcessorOutput;
import com.batch.model.SyncDictionaryUploadProcessorInput;
import com.batch.model.SyncDictionaryUploadProcessorOutput;
import com.batch.service.SendEmail;

public class SyncDictionaryUploadProcessor implements ItemProcessor<ProcessorInput, ProcessorOutput> {
	@Autowired
	private SendEmail sendEmail;
	
    public SyncDictionaryUploadProcessorOutput process(ProcessorInput processorInput) {
    	SyncDictionaryUploadProcessorInput dictionaryUploadProcessorInput = (SyncDictionaryUploadProcessorInput) processorInput;
    	List<LessonDictionary> dics = new LinkedList<LessonDictionary>();
    	try {
    		XSSFSheet sheet = dictionaryUploadProcessorInput.getSheet1();
			dics.addAll(getDictionariesFrom(sheet, true));
			sheet = dictionaryUploadProcessorInput.getSheet2();
			dics.addAll(getDictionariesFrom(sheet, false));
		} catch (Exception e) {
			try {
				sendEmail.sendEmail(e.getMessage(), "Sync With Error");
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
    	
		SyncDictionaryUploadProcessorOutput output = new SyncDictionaryUploadProcessorOutput();
    	output.setDictionaries(dics);
    	return output;
    }
    private List<LessonDictionary> getDictionariesFrom(XSSFSheet sheet, boolean isStandart) throws Exception {
		int count = 0;
		List<LessonDictionary> dics = new ArrayList<LessonDictionary>();
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {

			if (count == 0) {
				Row row = rowIterator.next();
				count++;
			} else {
				Row row = rowIterator.next();
				try {
					if (row.getCell(1) != null) {
						LessonDictionary dic = new LessonDictionary();
						if (isStandart) {
							dic.setHantu(row.getCell(5).getStringCellValue().replace("\'", "\\'"));
							dic.setHsk(new Double(row.getCell(1).getNumericCellValue()).intValue());
							dic.setLesson(String.valueOf(new Double(row.getCell(2).getNumericCellValue()).intValue()));
							dic.setPart(String.valueOf(new Double(row.getCell(3).getNumericCellValue()).intValue()));
							dic.setStandart(1);
							dic.setPinyin(row.getCell(6).getStringCellValue());
							dic.setNghia1(row.getCell(7).getStringCellValue());
							dic.setOrder(new Double(row.getCell(4).getNumericCellValue()).intValue());
						} else {
							dic.setHantu(row.getCell(1).getStringCellValue().replace("\'", "\\'"));
							dic.setHsk(new Double(row.getCell(0).getNumericCellValue()).intValue());
							dic.setPopular(1);
							dic.setPinyin(row.getCell(2).getStringCellValue());
							dic.setNghia1(row.getCell(3).getStringCellValue());
						}
						dics.add(dic);
					}
				} catch (Exception e) {
					System.out.println(Stream.of(e.getStackTrace()).map(item -> item.toString())
							.collect(Collectors.joining("\n")));
					if (isStandart) {
						throw new Exception(String.format("Có lỗi trên file upload ở từ %s. Pinyin %s",
								row.getCell(5).getStringCellValue(), row.getCell(6).getStringCellValue()));
					} else {
						throw new Exception(String.format("Có lỗi trên file upload ở từ %s. Pinyin %s",
								row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue()));
					}

				}
			}

		}
		return dics;
	}
}
