package com.batch.model;

import java.util.List;
import java.util.Map;

import com.batch.constant.Category;


public class QuestionDescription implements Cloneable {
	String type;
	String number;
	String header;
	Map<String, String> headingOptions;
	List<QuestionBody> body;
	Category category;
	String listenContent;
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public List<QuestionBody> getBody() {
		return body;
	}
	public void setBody(List<QuestionBody> body) {
		this.body = body;
	}
	public Map<String, String> getHeadingOptions() {
		return headingOptions;
	}
	public void setHeadingOptions(Map<String, String> headingOptions) {
		this.headingOptions = headingOptions;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getListenContent() {
		return listenContent;
	}
	public void setListenContent(String listenContent) {
		this.listenContent = listenContent;
	}
	
}
