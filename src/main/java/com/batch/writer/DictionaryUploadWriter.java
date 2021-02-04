package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.batch.dao.DictionaryDao;
import com.batch.model.DictionaryUploadProcessorOutput;
import com.batch.model.ProcessorOutput;

public class DictionaryUploadWriter implements ItemWriter<ProcessorOutput> {
	
	@Autowired
	private DictionaryDao dictionaryDao;
	
	@Override
	public void write(List<? extends ProcessorOutput> items) throws Exception {
		DictionaryUploadProcessorOutput output = (DictionaryUploadProcessorOutput) items.get(0);
		dictionaryDao.deleteAll();
		dictionaryDao.insertAll(output.getDictionaries());
	}

}
