package com.batch.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.batch.model.Transaction;
import com.batch.util.Util;

public class CustomItemReader implements ItemReader<Transaction> {

	private Integer count = 0;
	
	private Iterator<XSSFSheet> sheetsIterator;
	
	@Value("${uploadFolder}")
	private String uploadFolder;

	@Autowired
	private Util util;

	@Override
	public Transaction read() throws Exception {
		List<String> files = util.getAllCompleteFilesPathInFolder(uploadFolder);
		InputStream inputStream = new FileInputStream(new File(files.get(0)));
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		System.out.println("open");
		count++;
		return count == 1? new Transaction():null;
	}
}
