package com.artflow.artflow.dto;

import com.artflow.artflow.dto.common.ValidationConstants;
import jakarta.validation.constraints.Pattern;

public class ProjectTagCreateDto {
	@Pattern(
		regexp = ValidationConstants.PROJECT_NAME_REGEX,
		message = ValidationConstants.PROJECT_NAME_MESSAGE
	)
	private String projectName;
	@Pattern(
		regexp = ValidationConstants.TAG_REGEX,
		message = ValidationConstants.TAG_MESSAGE
	)
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
