package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.exception.ForbiddenActionException;
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
	private final VisibilityUtilService visibilityUtilService;
	private final ProjectTagService projectTagService;
	
	public ProjectService(UserProjectRepository projectRepo, UserRepository userRepo, ProjectTagService projectTagService, TagRepository tagRepo, VisibilityUtilService visibilityUtilService, ProjectTagRepository projectTagRepo) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.projectTagService = projectTagService;
		this.tagRepo = tagRepo;
		this.visibilityUtilService = visibilityUtilService;
		this.projectTagRepo = projectTagRepo;
	}
	
	@Transactional
	public ProjectDto create(String username, ProjectCreateDto projectInitDto, Long userId) {
		visibilityUtilService.checkUsernameAgainstId(userId, username);
		User user = userRepo.findByIdWithProjects(userId).get();
		
		if (projectRepo.findByOwner_UsernameAndProjectName(username, projectInitDto.getProjectName()).isPresent()) {
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
	
	public List<ProjectDto> getUserProjects(String username, Long userId, Optional<String> tagQuery, Optional<String> visQuery) {
		boolean publicOnly = !visibilityUtilService.doesUsernameBelongToId(userId, username);
		
		Set<String> tags = null;
		if (tagQuery.isPresent()) {
			tags = new HashSet<>(List.of(tagQuery.get().split(",")));
		}
		
		Visibility visibility = publicOnly ? Visibility.PUBLIC : null;
		try {
			visibility = Visibility.valueOf(visQuery.get().toUpperCase());
			if (publicOnly && visibility == Visibility.PRIVATE) {
				throw new ForbiddenActionException();
			}
		}
		catch (NoSuchElementException e) {
			// user didn't provide a visibility
		}
		catch (IllegalArgumentException e) {
			throw new QueryException("Invalid visibility: " + visQuery.get());
		}
		
		if (tags == null && visibility == null) {
			return toDto(projectRepo.findByOwner_UsernameOrderByCreatedDateTimeDesc(username));
		}
		if (tags == null) {
			return toDto(projectRepo.findByOwner_UsernameAndVisibilityOrderByCreatedDateTimeDesc(username, visibility));
		}
		if (visibility == null) {
			return toDto(projectRepo.findByUsernameAndTagsOrderByCreatedDateTime(username, tags));
		}
		else {
			return toDto(projectRepo.findByUsernameAndVisibilityAndTagsOrderByCreatedDateTime(username, visibility, tags));
		}
	}
	
	public ProjectDto getProject(String username, String projectName, Long userId) {
		return toDto(visibilityUtilService.getProjectCheckUsernameAgainstProjectVisibility(userId, username, projectName));
	}
	
	@Transactional
	public ProjectDto updateProject(String username, ProjectUpdateDto projectUpdateDto, Long userId) {
		visibilityUtilService.checkUsernameAgainstId(userId, username);
		
		UserProject project = projectRepo.findByIdWithTags(projectUpdateDto.getId())
				.orElseThrow(() -> new ProjectNotFoundException(projectUpdateDto.getProjectName(), username));
		Optional<UserProject> projectWithRequestedName = projectRepo.findByOwner_UsernameAndProjectName(username, projectUpdateDto.getProjectName());
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
	public void deleteProject(String username, String projectName, Long userId) {
		visibilityUtilService.checkUsernameAgainstId(userId, username);
		
		Optional<UserProject> foundProject = projectRepo.findByOwner_UsernameAndProjectNameWithTags(username, projectName);
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
			log.info("checking if project \"" + project.getProjectName() + "\" under user with username \"" + project.getOwner().getUsername() + "\" is already tagged with \"" + tagString + "\"");
			if (projectTagRepo.existsByTagNameAndProject_ProjectNameAndProject_Owner_Username(tagString, project.getProjectName(), project.getOwner().getUsername())) {
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

