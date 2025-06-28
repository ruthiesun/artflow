package com.artflow.artflow.dto;

import java.time.LocalDateTime;

public class ProjectImageCreateDto {
	private String caption;
	private LocalDateTime dateTime;
	private String url;
	
	public ProjectImageCreateDto(String caption, LocalDateTime dateTime, String url) {
		this.caption = caption;
		this.dateTime = dateTime;
		this.url = url;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
