
package com.batch.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.batch.constant.QuestionType;
import com.batch.model.Result;

@Component
public interface ResultDao {
	 void delete(int hsk, QuestionType questionType);
	 void insertResult(List<Result> results) throws Exception;
}