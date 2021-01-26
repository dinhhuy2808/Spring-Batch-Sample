package com.batch.model;

import com.batch.constant.QuestionType;

public class Result {
	private Integer hsk;
	private Integer test;
	private Integer number;
	private String answer;
	private String part;
	private QuestionType type;
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
	public Integer getTest() {
		return test;
	}
	public void setTest(Integer test) {
		this.test = test;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public QuestionType getType() {
		return type;
	}
	public void setType(String type) {
		this.type = QuestionType.valueOf(type);
	}
	
}
