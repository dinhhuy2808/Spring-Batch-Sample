package com.batch.model;

public class LessonDictionary {
	private Integer id;
	private String hantu;
	private String pinyin;
	private String nghia1;
	private String hanviet;
	private Integer hsk;
	private String lesson;
	private String part;
	private Integer standart;
	private Integer popular;
	private Integer refid;
	private Integer order;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHantu() {
		return hantu;
	}
	public void setHantu(String hantu) {
		this.hantu = hantu;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getNghia1() {
		return nghia1;
	}
	public void setNghia1(String nghia1) {
		this.nghia1 = nghia1;
	}
	public String getHanviet() {
		return hanviet;
	}
	public void setHanviet(String hanviet) {
		this.hanviet = hanviet;
	}
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
	public String getLesson() {
		return lesson;
	}
	public void setLesson(String lesson) {
		this.lesson = lesson;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public Integer getStandart() {
		return standart;
	}
	public void setStandart(Integer standart) {
		this.standart = standart;
	}
	public Integer getPopular() {
		return popular;
	}
	public void setPopular(Integer popular) {
		this.popular = popular;
	}
	public String getKey() {
		return this.hsk+"-"+this.lesson;
	}
	public Integer getRefid() {
		return refid;
	}
	public void setRefid(Integer refid) {
		this.refid = refid;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
}
