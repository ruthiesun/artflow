package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class ProjectImageCreateDto {
	@ValidByRule("projectImageCaption")
	private String caption;
	private LocalDateTime dateTime;
	@URL
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
