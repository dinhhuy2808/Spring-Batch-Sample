package com.batch.model;

import java.util.List;


public class SyncDictionaryUploadProcessorOutput extends ProcessorOutput {
	private List<LessonDictionary> dictionaries;

	public List<LessonDictionary> getDictionaries() {
		return dictionaries;
	}

	public void setDictionaries(List<LessonDictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	
}
