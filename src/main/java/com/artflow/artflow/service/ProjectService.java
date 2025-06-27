package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.exception.ProjectNameInUseException;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.exception.ProjectTagNameInUseException;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {
	private final UserProjectRepository projectRepo;
	private final UserRepository userRepo;
	private final ProjectTagService projectTagService;
	
	public ProjectService(UserProjectRepository projectRepo, UserRepository userRepo, ProjectTagService projectTagService) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.projectTagService = projectTagService;
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
	
	public List<ProjectDto> getUserProjects(String userEmail) {
		List<UserProject> projects = projectRepo.findByOwner_Email(userEmail);
		return toDto(projects);
	}

	public List<ProjectDto> getPublicUserProjects(String userEmail) {
		List<UserProject> projects = projectRepo.findByOwner_EmailAndVisibility(userEmail, Visibility.PUBLIC);
		return toDto(projects);
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

