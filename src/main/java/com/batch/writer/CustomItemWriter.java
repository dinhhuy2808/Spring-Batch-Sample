package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.batch.dao.ResultDao;
import com.batch.dao.impl.ResultDaoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.batch.model.ProcessorOutput;
import com.batch.util.Util;

public class CustomItemWriter implements ItemWriter<ProcessorOutput> {

	@Value("${lessonQuizPath}")
	private String lessonQuizPath;
	
	@Autowired
	private ResultDao resultDao;

	@Autowired
	private Util util;
	
	@Override
	public void write(List<? extends ProcessorOutput> items) throws Exception {
		ProcessorOutput output = items.get(0);
		resultDao.insertResult(output.getResults());
		util.writeToFile(util.objectToJSON(output.getQuestionDescriptions()),
				lessonQuizPath + "/hsk-" + output.getHsk() + "-" + output.getName().trim() + ".json");
	}

}
