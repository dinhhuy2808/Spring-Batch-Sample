
package com.batch.dao;

import java.util.List;

import com.batch.model.LessonDictionary;

public interface LessonDictionaryDao {
	 void deleteAll();
	 void insertAll(List<LessonDictionary> dictionaries) throws Exception;
	 void updateWordWithLesson(List<LessonDictionary> dictionaries) throws Exception;
}