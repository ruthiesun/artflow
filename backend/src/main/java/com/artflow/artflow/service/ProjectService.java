package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.exception.ProjectNameInUseException;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.exception.ProjectTagNameInUseException;
import com.artflow.artflow.exception.QueryException;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.TagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import jakarta.transaction.Transactional;
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
	private final UserProjectRepository projectRepo;
	private final UserRepository userRepo;
	private final TagRepository tagRepo;
	private final ProjectTagService projectTagService;
	
	public ProjectService(UserProjectRepository projectRepo, UserRepository userRepo, ProjectTagService projectTagService, TagRepository tagRepo) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.projectTagService = projectTagService;
		this.tagRepo = tagRepo;
	}
	
	@Transactional
	public ProjectDto create(ProjectCreateDto projectInitDto, String userEmail) {
		User user = userRepo.findByEmail(userEmail).get();
		if (projectRepo.findByOwner_EmailAndProjectName(userEmail, projectInitDto.getProjectName()).isPresent()) {
			throw new ProjectNameInUseException(projectInitDto.getProjectName());
		}
		UserProject project = new UserProject(user, projectInitDto.getProjectName());
		project.setDescription(projectInitDto.getDescription());
		setProjectVisibility(projectInitDto.getVisibility(), project);
		
		projectRepo.save(project);
		createAndAddTagsToProject(projectInitDto.getTagStrings(), project);
		return toDto(project);
	}
	
	public List<ProjectDto> getUserProjects(String userEmail, Optional<String> tagQuery, Optional<String> visQuery) {
		Set<String> tags = null;
		if (tagQuery.isPresent()) {
			tags = new HashSet<>(List.of(tagQuery.get().split(",")));
//			String[] tags =
//			for (String tag : tags) {
//				tagRepo.findByName(tag).map(Tag::getId).ifPresent(tagIds::add);
//			}
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
			return toDto(projectRepo.findByOwner_Email(userEmail));
		}
		if (tags == null) {
			return toDto(projectRepo.findByOwner_EmailAndVisibility(userEmail, visibility));
		}
		if (visibility == null) {
			return toDto(projectRepo.findByEmailAndTags(userEmail, tags));
		}
		else {
			return toDto(projectRepo.findByEmailAndVisibilityAndTags(userEmail, visibility, tags));
		}
	}

	public ProjectDto getProject(String projectName, String userEmail) {
		UserProject project = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, userEmail));
		return toDto(project);
	}
	
	@Transactional
	public ProjectDto updateProject(ProjectUpdateDto projectUpdateDto, String userEmail) {
		UserProject project = projectRepo.findById(projectUpdateDto.getId())
				.orElseThrow(() -> new ProjectNotFoundException(projectUpdateDto.getProjectName(), userEmail));
		Optional<UserProject> projectWithRequestedName = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectUpdateDto.getProjectName());
		if (projectWithRequestedName.isPresent() && !Objects.equals(projectWithRequestedName.get().getId(), project.getId())) {
			throw new ProjectNameInUseException(projectUpdateDto.getProjectName());
		}
		project.setProjectName(project.getProjectName());
		project.setDescription(projectUpdateDto.getDescription());
		project.setVisibility(projectUpdateDto.getVisibility());
		createAndAddTagsToProject(projectUpdateDto.getTagStrings(), project);
		return toDto(projectRepo.save(project));
	}
	
	@Transactional
	public void deleteProject(String projectName, String userEmail) {
		Optional<UserProject> foundProject = projectRepo.findByOwner_EmailAndProjectName(userEmail, projectName);
		if (foundProject.isEmpty()) {
			return;
		}
		projectRepo.delete(foundProject.get());
	}
	
	private void createAndAddTagsToProject(List<String> tagStrings, UserProject project) {
		if (tagStrings == null || tagStrings.isEmpty()) {
			return;
		}
		for (String tagString : tagStrings) {
			ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(project.getProjectName(), tagString);
			try {
				projectTagService.create(projectTagCreateDto, project.getOwner().getEmail());
			}
			catch (ProjectTagNameInUseException e) {
				// this is fine; user inputted duplicate tags
			}
		}
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

