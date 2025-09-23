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
import com.artflow.artflow.model.Visibility;
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
	private final VisibilityUtilService visibilityUtilService;
	
	public ProjectTagService(TagRepository tagRepo, ProjectTagRepository projectTagRepo, UserProjectRepository projectRepo, VisibilityUtilService visibilityUtilService) {
		this.tagRepo = tagRepo;
		this.projectTagRepo = projectTagRepo;
		this.projectRepo = projectRepo;
		this.visibilityUtilService = visibilityUtilService;
	}
	
	@Transactional
	public ProjectTagDto create(String username, ProjectTagCreateDto projectTagCreateDto, Long userId) {
		visibilityUtilService.checkUsernameAgainstId(userId, username);
		
		if (projectTagRepo.existsByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(
				projectTagCreateDto.getTagName(),
				projectTagCreateDto.getProjectName(),
				username)) {
			throw new ProjectTagNameInUseException(projectTagCreateDto.getTagName(), projectTagCreateDto.getProjectName());
		}
		UserProject project = projectRepo.findByOwner_UsernameIgnoreCaseAndProjectNameIgnoreCase(username, projectTagCreateDto.getProjectName())
				.orElseThrow(() -> new ProjectNotFoundException(projectTagCreateDto.getProjectName(), username));
		Tag tag = getOrCreateTag(projectTagCreateDto.getTagName().toLowerCase());
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag.getId()));
		projectTag.setTag(tag);
		projectTag.setProject(project);
		project.updateDateTime();
		return toDto(projectTagRepo.save(projectTag));
	}
	
	public ProjectTagDto getTagForProject(String username, String projectName, String tagName, Long userId) {
		visibilityUtilService.checkUsernameAgainstProjectVisibility(userId, username, projectName);
		
		ProjectTag projectTag = projectTagRepo.findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(tagName, projectName, username)
				.orElseThrow(() -> new ProjectTagNotFoundException(tagName, projectName));
		return toDto(projectTag);
	}
	
	public List<TagDto> getTags(String username, Long userId) {
		if (visibilityUtilService.doesUsernameBelongToId(userId, username)) {
			return toTagDto(projectTagRepo.findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCase(username));
		}
		else {
			return toTagDto(projectTagRepo.findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCaseAndProject_Visibility(username, Visibility.PUBLIC));
		}
	}
	
	public List<ProjectTagDto> getTagsForProject(String username, String projectName, Long userId) {
		visibilityUtilService.checkUsernameAgainstProjectVisibility(userId, username, projectName);
		
		List<ProjectTag> projectTags = projectTagRepo.findByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(projectName, username);
		return toDto(projectTags);
	}
	
	@Transactional
	public void deleteTag(String username, String projectName, String tagName, Long userId) {
		visibilityUtilService.checkUsernameAgainstId(userId, username);
		
		Optional<ProjectTag> projectTag = projectTagRepo.findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(tagName, projectName, username);
		if (projectTag.isPresent()) {
			projectTagRepo.delete(projectTag.get());
			projectTag.get().getProject().updateDateTime();
		}
	}
	
	@Transactional
	public Tag getOrCreateTag(String tagName) {
		return tagRepo.findByNameIgnoreCase(tagName)
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
