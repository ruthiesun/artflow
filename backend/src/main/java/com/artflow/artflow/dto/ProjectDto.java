package com.artflow.artflow.dto;

import com.artflow.artflow.model.Visibility;

import java.time.LocalDateTime;

public class ProjectDto {
	private Long id;
	private String projectName;
	private String description;
	private Visibility visibility;
	private LocalDateTime createdDateTime;
	private LocalDateTime updatedDateTime;
	
	public ProjectDto(Long id, String projectName, String description, Visibility visibility, LocalDateTime createdDateTime, LocalDateTime updatedDateTime) {
		this.id = id;
		this.projectName = projectName;
		this.description = description;
		this.visibility = visibility;
		this.createdDateTime = createdDateTime;
		this.updatedDateTime = updatedDateTime;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}
	
	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	public LocalDateTime getUpdatedDateTime() {
		return updatedDateTime;
	}
	
	public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}
}
