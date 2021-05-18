package com.batch.model;

public class Details {
	private String image;
	private String audio;
	private String description;
	
	public Details() {
	}
	public Details(String image, String audio, String description) {
		this.image = image;
		this.audio = audio;
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getAudio() {
		return audio;
	}
	public void setAudio(String audio) {
		this.audio = audio;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
