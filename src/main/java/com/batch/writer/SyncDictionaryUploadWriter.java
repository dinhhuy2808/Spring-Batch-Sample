package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.batch.dao.LessonDictionaryDao;
import com.batch.model.ProcessorOutput;
import com.batch.model.SyncDictionaryUploadProcessorOutput;

public class SyncDictionaryUploadWriter implements ItemWriter<ProcessorOutput> {
	
	@Autowired
	private LessonDictionaryDao lessonDictionaryDao;
	
	@Override
	public void write(List<? extends ProcessorOutput> items) throws Exception {
		SyncDictionaryUploadProcessorOutput output = (SyncDictionaryUploadProcessorOutput) items.get(0);
		lessonDictionaryDao.deleteAll();
		lessonDictionaryDao.insertAll(output.getDictionaries());
		lessonDictionaryDao.updateWordWithLesson(output.getDictionaries());
	}

}
