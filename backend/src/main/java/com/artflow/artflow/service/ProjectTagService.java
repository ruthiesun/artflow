package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.ProjectTagDto;
import com.artflow.artflow.dto.TagDto;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.exception.ProjectTagNameInUseException;
import com.artflow.artflow.exception.ProjectTagNotFoundException;
import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.repository.ProjectTagRepository;
import com.artflow.artflow.repository.TagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectTagService {
	private final TagRepository tagRepo;
	private final ProjectTagRepository projectTagRepo;
	private final UserProjectRepository projectRepo;
	
	public ProjectTagService(TagRepository tagRepo, ProjectTagRepository projectTagRepo, UserProjectRepository projectRepo) {
		this.tagRepo = tagRepo;
		this.projectTagRepo = projectTagRepo;
		this.projectRepo = projectRepo;
	}
	
	@Transactional
	public ProjectTagDto create(ProjectTagCreateDto projectTagCreateDto, String userEmail) {
		if (projectTagRepo.findByTagNameAndProject_ProjectNameAndProject_Owner_Email(
				projectTagCreateDto.getTagName(),
				projectTagCreateDto.getProjectName(),
				userEmail)
				.isPresent()) {
			throw new ProjectTagNameInUseException(projectTagCreateDto.getTagName(), projectTagCreateDto.getProjectName());
		}
		UserProject project = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectTagCreateDto.getProjectName())
				.orElseThrow(() -> new ProjectNotFoundException(projectTagCreateDto.getProjectName(), userEmail));
		Tag tag = getOrCreateTag(projectTagCreateDto.getTagName());
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag.getId()));
		projectTag.setTag(tag);
		projectTag.setProject(project);
		project.updateDateTime();
		return toDto(projectTagRepo.save(projectTag));
	}
	
	public ProjectTagDto getTagForProject(String projectName, String tagName, String email) {
		projectRepo.findByOwner_EmailAndProjectName(email, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, email));
		ProjectTag projectTag = projectTagRepo.findByTagNameAndProject_ProjectNameAndProject_Owner_Email(tagName, projectName, email)
				.orElseThrow(() -> new ProjectTagNotFoundException(tagName, projectName));
		return toDto(projectTag);
	}
	
	public List<TagDto> getTags(String userEmail) {
		return toTagDto(projectTagRepo.findDistinctTagNameByProject_Owner_Email(userEmail));
	}
	
	public List<ProjectTagDto> getTagsForProject(String projectName, String email) {
		List<ProjectTag> projectTags = projectTagRepo.findByProject_ProjectNameAndProject_Owner_Email(projectName, email);
		return toDto(projectTags);
	}
	
	@Transactional
	public void deleteTag(String projectName, String tagName, String email) {
		Optional<ProjectTag> projectTag = projectTagRepo.findByTagNameAndProject_ProjectNameAndProject_Owner_Email(tagName, projectName, email);
		if (projectTag.isPresent()) {
			projectTagRepo.delete(projectTag.get());
			projectTag.get().getProject().updateDateTime();
		}
	}
	
	@Transactional
	public Tag getOrCreateTag(String tagName) {
		return tagRepo.findByName(tagName)
				.orElseGet(() -> tagRepo.save(new Tag(tagName)));
	}
	
	private List<ProjectTagDto> toDto(List<ProjectTag> projectTags) {
		List<ProjectTagDto> projectTagDtos = new ArrayList<>();
		for (ProjectTag projectTag : projectTags) {
			projectTagDtos.add(toDto(projectTag));
		}
		return projectTagDtos;
	}
	
	private ProjectTagDto toDto(ProjectTag projectTag) {
		return new ProjectTagDto(
				projectTag.getProject().getProjectName(),
				projectTag.getTag().getName()
		);
	}
	
	private List<TagDto> toTagDto(List<String> tagNames) {
		List<TagDto> tagDtos = new ArrayList<>();
		for (String name : tagNames) {
			tagDtos.add(toTagDto(name));
		}
		return tagDtos;
	}
	
	private TagDto toTagDto(String tagName) {
		return new TagDto(
				tagName
		);
	}
}
