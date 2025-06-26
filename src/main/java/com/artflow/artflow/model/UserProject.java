package com.artflow.artflow.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_project", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_name"}))
public class UserProject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long id;

	@Column(name = "project_name", nullable = false)
	private String projectName;
	
	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "created_date_time", nullable = false)
	private LocalDateTime createdDateTime;
	
	@NotNull
	@Column(name = "updated_date_time", nullable = false)
	private LocalDateTime updatedDateTime;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "visibility", nullable = false)
	private Visibility visibility;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("position ASC")
	private List<ProjectImage> images = new ArrayList<>();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	private List<ProjectTag> projectTags = new ArrayList<>();
	
	@PrePersist
	public void prePersist() {
		initDateTime();
	}
	
	@PreUpdate
	public void preUpdate() {
		updateDateTime();
	}
	
	private void initDateTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		createdDateTime = currentTime;
		updatedDateTime = currentTime;
	}
	
	private void updateDateTime() {
		updatedDateTime = LocalDateTime.now();
	}
	
	public UserProject() {
		this.visibility = Visibility.PRIVATE;
	}
	
	public UserProject(User owner, String projectName) {
		this();
		this.owner = owner;
		this.projectName = projectName;
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
	
	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}
	
	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	public LocalDateTime getUpdatedDateTime() {
		return updatedDateTime;
	}
	
	public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public List<ProjectImage> getImages() {
		return images;
	}
	
	public void setImages(List<ProjectImage> images) {
		this.images = images;
	}
	
	public List<ProjectTag> getProjectTags() {
		return projectTags;
	}
	
	public void setProjectTags(List<ProjectTag> projectTags) {
		this.projectTags = projectTags;
	}
}

