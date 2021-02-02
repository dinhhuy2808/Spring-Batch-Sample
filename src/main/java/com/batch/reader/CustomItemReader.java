package com.batch.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.batch.constant.QuestionType;
import com.batch.dao.ResultDao;
import com.batch.model.ProcessorInput;
import com.batch.service.SendEmail;
import com.batch.util.Util;

public class CustomItemReader implements ItemReader<ProcessorInput> {

	private Iterator<Sheet> sheetsIterator;
	private Iterator<String> files;
	private List<String> filesName;
	private String hsk = "";
	private String file = "";
	
	@Value("${uploadFolder}")
	private String uploadFolder;

	@Value("#{systemProperties['spring.profiles.active']}")
	private String env;

	@Autowired
	private Util util;

	@Autowired
	private ResultDao resultDao;

	@Autowired
	private SendEmail sendEmail;

	@Override
	public ProcessorInput read() throws Exception {
		if (files == null) {
			files = util.getAllCompleteFilesPathInFolder(uploadFolder).iterator();
			filesName = util.getAllFilesNameInFolder(uploadFolder);
		}
		
		if (sheetsIterator == null) {
			file = readNextFile();
			if (file == null) {
				return null;
			}
			readALlfile();
			
		}
		
		ProcessorInput processorInput = readNextSheet(Integer.parseInt(hsk));
		if (processorInput == null) {
			file = readNextFile();
			if (file == null) {
				return null;
			}
			readALlfile();
			processorInput = readNextSheet(Integer.parseInt(hsk));
		}
		return processorInput;
	}
	
	private void readALlfile() throws IOException {
		InputStream inputStream = new FileInputStream(new File(file));
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		sheetsIterator = workbook.sheetIterator();
		sheetsIterator.next();//skip first sheet
		if (env.equalsIgnoreCase("production")) {
			hsk = file.substring(file.lastIndexOf("/")+1, file.length()).split("\\.")[0];
		} else {
			hsk = file.substring(file.lastIndexOf("\\")+1, file.length()).split("\\.")[0];
		}
		
		resultDao.delete(Integer.parseInt(hsk), QuestionType.QUIZ);
	}
	
	private ProcessorInput readNextSheet(int hsk) {
		if (sheetsIterator.hasNext()) {
			ProcessorInput input = new ProcessorInput();
			input.setHsk(hsk);
			input.setSheet(sheetsIterator.next());
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
			sendEmail.sendEmail("Upload file for hsk: "
					+ StringUtils.join(filesName, " ") + " successfully !!!");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
