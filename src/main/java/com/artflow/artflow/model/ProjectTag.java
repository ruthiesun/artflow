package com.artflow.artflow.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_tag")
public class ProjectTag {
	@EmbeddedId
	private ProjectTagId id;
	
	@MapsId("tagId")
	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag tag;
	
	@MapsId("userProjectId")
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "user_id", referencedColumnName = "user_id"),
			@JoinColumn(name = "project_name", referencedColumnName = "project_name")
	})
	private UserProject project;
	
	public ProjectTag() {}
	
	public ProjectTag(ProjectTagId id) {
		this.id = id;
	}
	
	public ProjectTagId getId() {
		return id;
	}
	
	public void setId(ProjectTagId id) {
		this.id = id;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public UserProject getProject() {
		return project;
	}
	
	public void setProject(UserProject project) {
		this.project = project;
	}
}
