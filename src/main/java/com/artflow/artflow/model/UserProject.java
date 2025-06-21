package com.artflow.artflow.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "user_project")
public class UserProject {
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "projectName", column = @Column(name = "project_name"))
	})
	private UserProjectId id;
	
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
	
	@MapsId("userId")
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	@OrderBy("position ASC")
	private List<ProjectImage> images = new ArrayList<>();
	
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
	
	public UserProject(UserProjectId id) {
		this();
		this.id = id;
	}
	
	public UserProjectId getId() {
		return id;
	}
	
	public void setId(UserProjectId id) {
		this.id = id;
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
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<ProjectImage> getImages() {
		return images;
	}

	public void setImages(List<ProjectImage> images) {
		this.images = images;
	}
	
}

