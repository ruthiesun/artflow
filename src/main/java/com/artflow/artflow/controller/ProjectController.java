package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(UriUtil.BASE + UriUtil.PROJECTS)
public class ProjectController {
	
	private final ProjectService projectService;
	
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectDto> create(@RequestBody ProjectCreateDto projectCreateDto, @AuthenticationPrincipal AuthUser user) {
		ProjectDto projectDto = projectService.create(projectCreateDto, user.email());
		return ResponseEntity
				.created(URI.create(UriUtil.getProjectUri(UriUtil.toSlug(projectDto.getProjectName()))))
				.body(projectDto);
	}
	
	@GetMapping
	public ResponseEntity<List<ProjectDto>> getMyProjects(@AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectService.getUserProjects(user.email()));
	}
	
	@GetMapping(UriUtil.PUBLIC_PROJECTS)
	public ResponseEntity<List<ProjectDto>> getMyPublicProjects(@AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectService.getPublicUserProjects(user.email()));
	}
	
	@GetMapping(UriUtil.PROJECT)
	public ResponseEntity<ProjectDto> getProject(@PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectService.getProject(projectName, user.email()));
	}
	
	@PutMapping()
	public ResponseEntity<ProjectDto> update(@RequestBody ProjectUpdateDto projectUpdateDto, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectService.updateProject(projectUpdateDto, user.email()));
	}
	
	@DeleteMapping(UriUtil.PROJECT)
	public ResponseEntity<Void> delete(@PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		projectService.deleteProject(UriUtil.fromSlug(projectName), user.email());
		return ResponseEntity.noContent().build();
	}
}

