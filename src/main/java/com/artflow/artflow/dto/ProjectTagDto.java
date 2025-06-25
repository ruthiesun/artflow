package com.artflow.artflow.dto;

public class ProjectTagDto {
	private String projectName;
	private String tagName;
	
	public ProjectTagDto(String projectName, String tagName) {
		this.projectName = projectName;
		this.tagName = tagName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
