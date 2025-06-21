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
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_image")
public class ProjectImage {
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "position", column = @Column(name = "position"))
	})
	private ProjectImageId id;
	
	@Column(name = "caption")
	private String caption;
	
	@Column(name = "date_time")
	private LocalDateTime dateTime;
	
	@NotNull
	@Column(name = "url", nullable = false)
	private String url;
	
	@MapsId("userProjectId")
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "user_id", referencedColumnName = "user_id"),
			@JoinColumn(name = "project_name", referencedColumnName = "project_name")
	})
	private UserProject project;
	
	public ProjectImage() {}
	
	public ProjectImage(ProjectImageId id, String url) {
		this.id = id;
		this.url = url;
	}
	
	public ProjectImageId getId() {
		return id;
	}
	
	public void setId(ProjectImageId id) {
		this.id = id;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public UserProject getProject() {
		return project;
	}

	public void setProject(UserProject project) {
		this.project = project;
	}
	
}
