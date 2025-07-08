package com.artflow.artflow.model;

import com.artflow.artflow.service.ProjectService;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "project_tag")
public class ProjectTag {
	private static final Logger log = LoggerFactory.getLogger(ProjectTag.class);
	@EmbeddedId
	private ProjectTagId id;
	
	@MapsId("tagId")
	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag tag;
	
	@MapsId("userProjectId")
	@ManyToOne
	@JoinColumn(name = "project_id", referencedColumnName = "project_id")
	private UserProject project;
	
	@PrePersist
	public void checkValues() {
		log.info("tag_id=" + id.getTagId());
		log.info("project_id=" + id.getUserProjectId());
		log.info("project_name=" + project.getProjectName());
	}
	
	public ProjectTag() {}
	
	public ProjectTag(Tag tag, UserProject project) {
		this.tag = tag;
		this.project = project;
	}
	
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
