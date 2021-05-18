package com.batch.model;

import java.util.List;
import java.util.Map;

import com.batch.constant.Category;


public class RawQuestionDescription implements Cloneable {
	String type;
	String number;
	Details header;
	Map<String, Details> headingOptions;
	List<RawQuestionBody> body;
	Category category;
	String listenContent;
	public RawQuestionDescription(String type, String number, Details header, Map<String, Details> headingOptions,
			List<RawQuestionBody> body, Category category, String listenContent) {
		this.type = type;
		this.number = number;
		this.header = header;
		this.headingOptions = headingOptions;
		this.body = body;
		this.category = category;
		this.listenContent = listenContent;
	}
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
	public Details getHeader() {
		return header;
	}
	public void setHeader(Details header) {
		this.header = header;
	}
	public List<RawQuestionBody> getBody() {
		return body;
	}
	public void setBody(List<RawQuestionBody> body) {
		this.body = body;
	}
	public Map<String, Details> getHeadingOptions() {
		return headingOptions;
	}
	public void setHeadingOptions(Map<String, Details> headingOptions) {
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
