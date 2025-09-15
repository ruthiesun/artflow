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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(UriUtil.BASE + UriUtil.USERNAME + UriUtil.PROJECTS)
public class ProjectController {
	
	private final ProjectService projectService;
	
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectDto> create(@PathVariable String username, @RequestBody ProjectCreateDto projectCreateDto, @AuthenticationPrincipal AuthUser user) {
		ProjectDto projectDto = projectService.create(username, projectCreateDto, user.id());
		return ResponseEntity
				.created(URI.create(UriUtil.getProjectUri(
					UriUtil.toSlug(username),
					UriUtil.toSlug(projectDto.getProjectName()))))
				.body(projectDto);
	}
	
	@GetMapping
	public ResponseEntity<List<ProjectDto>> getProjects(@PathVariable String username, @AuthenticationPrincipal AuthUser user,
														@RequestParam Optional<String> tags, @RequestParam Optional<String> visibility) {
		Long id = user == null ? null : user.id();
		return ResponseEntity.ok(projectService.getUserProjects(username, id, tags, visibility));
	}
	
	@GetMapping(UriUtil.PROJECT)
	public ResponseEntity<ProjectDto> getProject(@PathVariable String username, @PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		Long id = user == null ? null : user.id();
		return ResponseEntity.ok(projectService.getProject(username, projectName, id));
	}
	
	@PutMapping()
	public ResponseEntity<ProjectDto> update(@PathVariable String username, @RequestBody ProjectUpdateDto projectUpdateDto, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectService.updateProject(username, projectUpdateDto, user.id()));
	}
	
	@DeleteMapping(UriUtil.PROJECT)
	public ResponseEntity<Void> delete(@PathVariable String username, @PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		projectService.deleteProject(username, UriUtil.fromSlug(projectName), user.id());
		return ResponseEntity.noContent().build();
	}
}

