package com.batch.model;

import java.util.HashMap;
import java.util.Map;

public class QuestionBody implements Cloneable{
	String number;
	String header;
	Map<String, String> value = new HashMap<String, String>();
	String listenContent;
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
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public Map<String, String> getValue() {
		return value;
	}
	public void setValue(Map<String, String> value) {
		this.value = value;
	}
	public String getListenContent() {
		return listenContent;
	}
	public void setListenContent(String listenContent) {
		this.listenContent = listenContent;
	}
	
}
