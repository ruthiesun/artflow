package com.artflow.artflow.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_image", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "position"}))
public class ProjectImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;
	
	@Column(name = "position", nullable = false)
	private int position;
	
	@Column(name = "caption")
	private String caption;
	
	@Column(name = "date_time")
	private LocalDateTime dateTime;
	
	@NotNull
	@Column(name = "url", nullable = false)
	private String url;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	private UserProject project;
	
	public ProjectImage() {}
	
	public ProjectImage(UserProject project, int position, String url) {
		this.project = project;
		this.position = position;
		this.url = url;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
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
