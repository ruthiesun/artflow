package com.artflow.artflow.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectImageId implements Serializable {
	@Embedded
	private UserProjectId userProjectId;

	private int position;
	
	@Override
	public int hashCode() {
		return Objects.hash(userProjectId, position);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ProjectImageId)) return false;
		ProjectImageId that = (ProjectImageId) obj;
		return Objects.equals(userProjectId, that.userProjectId) &&
				Objects.equals(position, that.position);
	}
	
	public ProjectImageId() {}
	
	public ProjectImageId(UserProjectId userProjectId, int position) {
		this.userProjectId = userProjectId;
		this.position = position;
	}
	
	public UserProjectId getUserProjectId() {
		return userProjectId;
	}
	
	public void setUserProjectId(UserProjectId userProjectId) {
		this.userProjectId = userProjectId;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
}
