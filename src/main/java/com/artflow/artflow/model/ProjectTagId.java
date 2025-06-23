package com.artflow.artflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectTagId implements Serializable {
	
	private Long userProjectId;
	
	private Long tagId;
	
	@Override
	public int hashCode() {
		return Objects.hash(userProjectId, tagId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ProjectTagId)) return false;
		ProjectTagId that = (ProjectTagId) obj;
		return Objects.equals(userProjectId, that.userProjectId) &&
				Objects.equals(tagId, that.tagId);
	}
	
	public ProjectTagId() {}
	
	public ProjectTagId(Long userProjectId, Long tagId) {
		this.userProjectId = userProjectId;
		this.tagId = tagId;
	}
	
	public Long getUserProjectId() {
		return userProjectId;
	}
	
	public void setUserProjectId(Long userProjectId) {
		this.userProjectId = userProjectId;
	}
	
	public Long getTagId() {
		return tagId;
	}
	
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
}
