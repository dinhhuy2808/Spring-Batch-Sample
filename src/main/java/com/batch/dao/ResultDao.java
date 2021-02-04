
package com.batch.dao;

import java.util.List;

import com.batch.constant.QuestionType;
import com.batch.model.Result;

public interface ResultDao {
	 void delete(int hsk, QuestionType questionType);
	 void insertResult(List<Result> results) throws Exception;
}