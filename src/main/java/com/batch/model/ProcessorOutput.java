package com.batch.model;

import java.util.List;


public class ProcessorOutput {
	private List<Result> results;
	private List<QuestionDescription> questionDescriptions;
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
}
