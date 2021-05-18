package com.batch.model;

import java.util.List;


public class ExerciseUploadProcessorOutput extends ProcessorOutput {
	private List<Result> results;
	private List<QuestionDescription> questionDescriptions;
	private List<RawQuestionDescription> rawQuestionDescriptions;
	private String hsk;
	private String name;
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public List<QuestionDescription> getQuestionDescriptions() {
		return questionDescriptions;
	}
	public void setQuestionDescriptions(List<QuestionDescription> questionDescriptions) {
		this.questionDescriptions = questionDescriptions;
	}
	public String getHsk() {
		return hsk;
	}
	public void setHsk(String hsk) {
		this.hsk = hsk;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RawQuestionDescription> getRawQuestionDescriptions() {
		return rawQuestionDescriptions;
	}
	public void setRawQuestionDescriptions(List<RawQuestionDescription> rawQuestionDescriptions) {
		this.rawQuestionDescriptions = rawQuestionDescriptions;
	}
}
