package com.artflow.artflow.dto;

import com.artflow.artflow.model.Visibility;

import java.util.List;

public class ProjectCreateDto {
	private String projectName;
	private String description;
	private Visibility visibility;
	private List<String> tagStrings;
	
	public ProjectCreateDto() {
	
	}
	
	public ProjectCreateDto(String projectName, String description, Visibility visibility) {
		this.projectName = projectName;
		this.description = description;
		this.visibility = visibility;
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
	
	public List<String> getTagStrings() {
		return tagStrings;
	}
	
	public void setTagStrings(List<String> tagStrings) {
		this.tagStrings = tagStrings;
	}
}
