package com.batch.model;

import java.util.List;


public class DictionaryUploadProcessorOutput extends ProcessorOutput {
	private List<Dictionary> dictionaries;

	public List<Dictionary> getDictionaries() {
		return dictionaries;
	}

	public void setDictionaries(List<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	
}
