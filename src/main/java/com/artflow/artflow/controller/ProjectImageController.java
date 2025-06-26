package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.ProjectImageCreateDto;
import com.artflow.artflow.dto.ProjectImageDto;
import com.artflow.artflow.dto.ProjectImageUpdateDto;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.ProjectImageService;
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
@RequestMapping("/api/projects/images")
public class ProjectImageController {
	private final ProjectImageService projectImageService;
	
	public ProjectImageController(ProjectImageService projectImageService) {
		this.projectImageService = projectImageService;
	}
	
	@PostMapping("/{projectName}")
	public ResponseEntity<ProjectImageDto> create(@PathVariable String projectName, @RequestBody ProjectImageCreateDto projectImageCreateDto, @AuthenticationPrincipal AuthUser user) {
		ProjectImageDto projectImageDto = projectImageService.create(projectName, projectImageCreateDto, user.email());
		return ResponseEntity
				.created(URI.create("/api/projects/images/" +
						UriUtil.toSlug(projectImageDto.getProjectName()) +
						"/" +
						UriUtil.toSlug(Long.toString(projectImageDto.getId()))))
				.body(projectImageDto);
	}
	
	@GetMapping("/{projectName}")
	public ResponseEntity<List<ProjectImageDto>> getImagesForProject(@PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectImageService.getImagesForProject(UriUtil.fromSlug(projectName), user.email()));
	}
	
	@GetMapping("/{projectName}/{imageId}")
	public ResponseEntity<ProjectImageDto> getImageForProject(@PathVariable String projectName, @PathVariable Long imageId, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectImageService.getImageForProject(UriUtil.fromSlug(projectName), imageId, user.email()));
	}
	@PutMapping()
	public ResponseEntity<ProjectImageDto> update(@RequestBody ProjectImageUpdateDto projectImageUpdateDto) {
		return ResponseEntity.ok(projectImageService.updateProjectImage(projectImageUpdateDto));
	}
	
	@DeleteMapping("/{projectName}/{imageId}")
	public ResponseEntity<Void> delete(@PathVariable String projectName, @PathVariable Long imageId, @AuthenticationPrincipal AuthUser user) {
		projectImageService.deleteProjectImage(imageId);
		return ResponseEntity.noContent().build();
	}
}
