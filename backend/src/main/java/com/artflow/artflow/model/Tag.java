package com.artflow.artflow.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;
	
	@NotNull
	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
	private List<ProjectTag> projectTags = new ArrayList<>();
	
	public Tag() {}
	
	public Tag(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<ProjectTag> getProjectTags() {
		return projectTags;
	}
	
	public void setProjectTags(List<ProjectTag> projectTags) {
		this.projectTags = projectTags;
	}
}
