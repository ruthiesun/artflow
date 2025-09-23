package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import com.artflow.artflow.model.Visibility;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProjectUpdateDto {
	private Long id;
	@ValidByRule("projectName")
	private String projectName;
	@ValidByRule("projectDescription")
	private String description;
	private Visibility visibility;
	private List<@ValidByRule("tag") String> tagStrings;
	
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
