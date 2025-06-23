package com.artflow.artflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserProjectId implements Serializable {
	private Long userId;
	
	private Long projectId;
	
	@Override
	public int hashCode() {
		return Objects.hash(userId, projectId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof UserProjectId)) return false;
		UserProjectId that = (UserProjectId) obj;
		return Objects.equals(userId, that.userId) &&
				Objects.equals(projectId, that.projectId);
	}
	
	public UserProjectId() {}
	
	public UserProjectId(Long userId, Long projectId) {
		this.userId = userId;
		this.projectId = projectId;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getProjectId() {
		return projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
