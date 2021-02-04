
package com.batch.dao;

import java.util.List;

import com.batch.model.Dictionary;

public interface DictionaryDao {
	 void deleteAll();
	 void insertAll(List<Dictionary> dictionaries) throws Exception;
}