package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.exception.ProjectNameInUseException;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.exception.QueryException;
import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.ProjectTagRepository;
import com.artflow.artflow.repository.TagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {
	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
	private final UserProjectRepository projectRepo;
	private final UserRepository userRepo;
	private final TagRepository tagRepo;
	private final ProjectTagRepository projectTagRepo;
	private final ProjectTagService projectTagService;
	
	public ProjectService(UserProjectRepository projectRepo, UserRepository userRepo, ProjectTagService projectTagService, TagRepository tagRepo, ProjectTagRepository projectTagRepo) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.projectTagService = projectTagService;
		this.tagRepo = tagRepo;
		this.projectTagRepo = projectTagRepo;
	}
	
	@Transactional
	public ProjectDto create(ProjectCreateDto projectInitDto, String userEmail) {
		User user = userRepo.findByEmailWithProjects(userEmail).get();
		if (projectRepo.findByOwner_EmailAndProjectName(userEmail, projectInitDto.getProjectName()).isPresent()) {
			throw new ProjectNameInUseException(projectInitDto.getProjectName());
		}
		UserProject project = new UserProject(user, projectInitDto.getProjectName());
		project.setDescription(projectInitDto.getDescription());
		setProjectVisibility(projectInitDto.getVisibility(), project);
		
		projectRepo.save(project);
		ensureProjectTags(projectInitDto.getTagStrings(), project);
		user.getProjects().add(project);
		return toDto(project);
	}
	
	public List<ProjectDto> getUserProjects(String userEmail, Optional<String> tagQuery, Optional<String> visQuery) {
		Set<String> tags = null;
		if (tagQuery.isPresent()) {
			tags = new HashSet<>(List.of(tagQuery.get().split(",")));
		}
		
		Visibility visibility = null;
		try {
			visibility = Visibility.valueOf(visQuery.get().toUpperCase());
		}
		catch (NoSuchElementException e) {
			// user didn't provide a visibility
		}
		catch (IllegalArgumentException e) {
			throw new QueryException("Invalid visibility: " + visQuery.get());
		}
		
		if (tags == null && visibility == null) {
			return toDto(projectRepo.findByOwner_EmailOrderByCreatedDateTimeDesc(userEmail));
		}
		if (tags == null) {
			return toDto(projectRepo.findByOwner_EmailAndVisibilityOrderByCreatedDateTimeDesc(userEmail, visibility));
		}
		if (visibility == null) {
			return toDto(projectRepo.findByEmailAndTagsOrderByCreatedDateTime(userEmail, tags));
		}
		else {
			return toDto(projectRepo.findByEmailAndVisibilityAndTagsOrderByCreatedDateTime(userEmail, visibility, tags));
		}
	}
	
	public ProjectDto getProject(String projectName, String userEmail) {
		UserProject project = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, userEmail));
		return toDto(project);
	}
	
	@Transactional
	public ProjectDto updateProject(ProjectUpdateDto projectUpdateDto, String userEmail) {
		UserProject project = projectRepo.findByIdWithTags(projectUpdateDto.getId())
				.orElseThrow(() -> new ProjectNotFoundException(projectUpdateDto.getProjectName(), userEmail));
		Optional<UserProject> projectWithRequestedName = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectUpdateDto.getProjectName());
		if (projectWithRequestedName.isPresent() && !Objects.equals(projectWithRequestedName.get().getId(), project.getId())) {
			throw new ProjectNameInUseException(projectUpdateDto.getProjectName());
		}
		project.setProjectName(projectUpdateDto.getProjectName());
		project.setDescription(projectUpdateDto.getDescription());
		project.setVisibility(projectUpdateDto.getVisibility());
		ensureProjectTags(projectUpdateDto.getTagStrings(), project);
		return toDto(projectRepo.save(project));
	}
	
	@Transactional
	public void deleteProject(String projectName, String userEmail) {
		Optional<UserProject> foundProject = projectRepo.findByOwner_EmailAndProjectNameWithTags(userEmail, projectName);
		if (foundProject.isEmpty()) {
			return;
		}
		UserProject project = foundProject.get();
		
		for (ProjectTag projectTag : project.getProjectTags()) {
			Tag tag = projectTag.getTag();
			tag.getProjectTags().remove(projectTag);

			if (tag.getProjectTags().isEmpty()) {
				tagRepo.delete(tag);
			}
		}
		
		project.getOwner().getProjects().remove(project);
	}
	
	private void ensureProjectTags(List<String> tagStrings, UserProject project) {
		if (tagStrings == null) {
			tagStrings = new ArrayList<>();
		}
		
		List<ProjectTag> tagsToRemove = new ArrayList<>();
		for (ProjectTag projectTag : project.getProjectTags()) {
			if (!tagStrings.contains(projectTag.getTag().getName())) {
				projectTag.getTag().getProjectTags().remove(projectTag);
				if (projectTag.getTag().getProjectTags().isEmpty()) {
					tagRepo.delete(projectTag.getTag());
				}
				tagsToRemove.add(projectTag);
			}
		}
		
		project.getProjectTags().removeAll(tagsToRemove);
		
		for (String tagString : tagStrings) {
			log.info("checking if project \"" + project.getProjectName() + "\" under user with email \"" + project.getOwner().getEmail() + "\" is already tagged with \"" + tagString + "\"");
			if (projectTagRepo.existsByTagNameAndProject_ProjectNameAndProject_Owner_Email(tagString, project.getProjectName(), project.getOwner().getEmail())) {
				log.info("project " + project.getProjectName() + " already contains tag " + tagString);
				continue; // project already contains tag
			}

			Tag tag = projectTagService.getOrCreateTag(tagString);
			log.info("creating new project tag with id \"" + tag.getId() + "\" and name \"" + tag.getName() + "\" for project \"" + project.getProjectName()+ "\"");
			ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag.getId()));
			projectTag.setTag(tag);
			projectTag.setProject(project);
			tag.getProjectTags().add(projectTag);
			project.getProjectTags().add(projectTag);
		}
		projectTagRepo.flush();
	}
	
	private void setProjectVisibility(Visibility visibility, UserProject project) {
		if (visibility == null) {
			project.setVisibility(Visibility.PRIVATE);
		}
		else {
			project.setVisibility(visibility);
		}
	}
	
	private List<ProjectDto> toDto(List<UserProject> projects) {
		List<ProjectDto> projectDtos = new ArrayList<>();
		for (UserProject project : projects) {
			projectDtos.add(toDto(project));
		}
		return projectDtos;
	}
	
	private ProjectDto toDto(UserProject project) {
		return new ProjectDto(
				project.getId(),
				project.getProjectName(),
				project.getDescription(),
				project.getVisibility(),
				project.getCreatedDateTime(),
				project.getUpdatedDateTime()
		);
	}
}

