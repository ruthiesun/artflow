package com.artflow.artflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserProjectId implements Serializable {
	private Long userId;
	
	private String projectName;
	
	@Override
	public int hashCode() {
		return Objects.hash(userId, projectName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof UserProjectId)) return false;
		UserProjectId that = (UserProjectId) obj;
		return Objects.equals(userId, that.userId) &&
				Objects.equals(projectName, that.projectName);
	}
	
	public UserProjectId() {}
	
	public UserProjectId(Long userId, String projectName) {
		this.userId = userId;
		this.projectName = projectName;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
