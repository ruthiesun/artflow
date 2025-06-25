package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.ProjectTagDto;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.ProjectTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projecttags")
public class ProjectTagController {
	private final ProjectTagService projectTagService;
	
	public ProjectTagController(ProjectTagService projectTagService) {
		this.projectTagService = projectTagService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectTagDto> create(@RequestBody ProjectTagCreateDto projectTagCreateDto, @AuthenticationPrincipal AuthUser user) {
		ProjectTagDto tagDto = projectTagService.create(projectTagCreateDto, user.email());
		return ResponseEntity
				.created(URI.create("/api/projecttags/" +
						UriUtil.toSlug(projectTagCreateDto.getProjectName()) +
						"/" +
						UriUtil.toSlug(tagDto.getTagName())))
				.body(tagDto);
	}
	
	@GetMapping("/{projectName}/{tagName}")
	public ResponseEntity<ProjectTagDto> get(@PathVariable String projectName, @PathVariable String tagName) {
		return ResponseEntity.ok(projectTagService.getTagForProject(
				UriUtil.fromSlug(projectName),
				UriUtil.fromSlug(tagName)));
	}
	
	@GetMapping
	public ResponseEntity<List<ProjectTagDto>> get(@AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectTagService.getTags(user.email()));
	}
	
	@GetMapping("/{projectName}")
	public ResponseEntity<List<ProjectTagDto>> getMyPublicProjects(@PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectTagService.getTagsForProject(UriUtil.fromSlug(projectName)));
	}
	
	@DeleteMapping("/{projectName}/{tagName}")
	public ResponseEntity<Void> delete(@PathVariable String projectName, @PathVariable String tagName) {
		projectTagService.deleteTag(
				UriUtil.fromSlug(projectName),
				UriUtil.fromSlug(tagName));
		return ResponseEntity.noContent().build();
	}

}
