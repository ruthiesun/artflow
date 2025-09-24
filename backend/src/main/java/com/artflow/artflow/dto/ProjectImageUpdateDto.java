package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class ProjectImageUpdateDto {
	private Long id;
	private Integer position;
	@ValidByRule("projectImageCaption")
	private String caption;
	private LocalDateTime dateTime;
	@URL
	@ValidByRule("projectImageUrl")
	private String url;
	
	public ProjectImageUpdateDto(Long id, Integer position, String caption, LocalDateTime dateTime, String url) {
		this.id = id;
		this.position = position;
		this.caption = caption;
		this.dateTime = dateTime;
		this.url = url;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
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
