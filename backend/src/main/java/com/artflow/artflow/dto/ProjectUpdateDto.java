package com.artflow.artflow.dto;

import com.artflow.artflow.dto.common.ValidationConstants;
import com.artflow.artflow.model.Visibility;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProjectUpdateDto {
	private Long id;
	@Pattern(
		regexp = ValidationConstants.PROJECT_NAME_REGEX,
		message = ValidationConstants.PROJECT_NAME_MESSAGE
	)
	private String projectName;
	@Size(max = ValidationConstants.PROJECT_DESC_LENGTH_MAX)
	private String description;
	private Visibility visibility;
	private List<
		@Pattern(
			regexp = ValidationConstants.TAG_REGEX,
			message = ValidationConstants.TAG_MESSAGE
		)
		@Size(max = ValidationConstants.TAG_LENGTH_MAX)
			String> tagStrings;
	
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
