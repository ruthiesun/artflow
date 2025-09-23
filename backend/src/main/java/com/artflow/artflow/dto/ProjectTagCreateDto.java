package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import jakarta.validation.constraints.Pattern;

public class ProjectTagCreateDto {
	@ValidByRule("projectName")
	private String projectName;
	@ValidByRule("tag")
	private String tagName;
	
	public ProjectTagCreateDto(String projectName, String tagName) {
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
