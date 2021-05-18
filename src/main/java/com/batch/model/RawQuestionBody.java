package com.batch.model;

import java.util.HashMap;
import java.util.Map;

public class RawQuestionBody implements Cloneable{
	String number;
	Details header;
	Map<String, Details> value = new HashMap<String, Details>();
	String listenContent;
	
	public RawQuestionBody(String number, Details header, Map<String, Details> value, String listenContent) {
		this.number = number;
		this.header = header;
		this.value = value;
		this.listenContent = listenContent;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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
	public Map<String, Details> getValue() {
		return value;
	}
	public void setValue(Map<String, Details> value) {
		this.value = value;
	}
	public String getListenContent() {
		return listenContent;
	}
	public void setListenContent(String listenContent) {
		this.listenContent = listenContent;
	}
	
}
