package com.batch.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.batch.model.DictionaryUploadProcessorInput;
import com.batch.model.ProcessorInput;
import com.batch.model.SyncDictionaryUploadProcessorInput;
import com.batch.service.SendEmail;
import com.batch.util.Util;

public class SyncDictionaryUploadReader implements ItemReader<ProcessorInput> {

	private Iterator<XSSFSheet> sheetsIterator;
	private Iterator<String> files;
	private List<String> filesName;
	private String file = "";
	
	@Value("${uploadDictionaryFolder}")
	private String uploadFolder;

	@Value("#{systemProperties['spring.profiles.active']}")
	private String env;

	@Autowired
	private Util util;

	@Autowired
	private SendEmail sendEmail;

	@Override
	public SyncDictionaryUploadProcessorInput read() throws Exception {
		if (files == null) {
			files = util.getAllCompleteFilesPathInFolder(uploadFolder).iterator();
			filesName = util.getAllFilesNameInFolder(uploadFolder);
		}
		
		if (sheetsIterator == null) {
			file = readNextFile();
			if (file == null) {
				return null;
			}
			readAllfile();
			
		}
		
		SyncDictionaryUploadProcessorInput processorInput = readNextSheet();
		if (processorInput == null) {
			file = readNextFile();
			if (file == null) {
				return null;
			}
			readAllfile();
			processorInput = readNextSheet();
		}
		return processorInput;
	}
	
	private void readAllfile() throws IOException {
		InputStream inputStream = new FileInputStream(new File(file));
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		sheetsIterator = Arrays.asList(workbook.getSheetAt(0), workbook.getSheetAt(1)).iterator();
	}
	
	private SyncDictionaryUploadProcessorInput readNextSheet() {
		if (sheetsIterator.hasNext()) {
			SyncDictionaryUploadProcessorInput input = new SyncDictionaryUploadProcessorInput();
			input.setSheet1(sheetsIterator.next());
			input.setSheet2(sheetsIterator.next());
			return input;
		} else {
			sheetsIterator = null;
			util.deleteFile(file);
			return null;
		}
	}
	
	private String readNextFile() {
		if (files.hasNext()) {
			return files.next();
		}
		try {
			sendEmail.sendEmail("Sync dictionary successfully !!!", "Upload successfully");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
