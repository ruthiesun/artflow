package com.artflow.artflow.dto;

import com.artflow.artflow.model.Visibility;

import java.util.List;

public class ProjectUpdateDto {
	private Long id;
	private String projectName;
	private String description;
	private Visibility visibility;
	private List<String> tagStrings;
	
	public ProjectUpdateDto() {
	
	}
	
	public ProjectUpdateDto(Long id, String projectName, String description, Visibility visibility) {
		this.id = id;
		this.projectName = projectName;
		this.description = description;
		this.visibility = visibility;
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
	
	public List<String> getTagStrings() {
		return tagStrings;
	}
	
	public void setTagStrings(List<String> tagStrings) {
		this.tagStrings = tagStrings;
	}
}
