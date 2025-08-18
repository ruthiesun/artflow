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
@RequestMapping(UriUtil.BASE + UriUtil.USERNAME + UriUtil.PROJECT + UriUtil.IMAGES)
public class ProjectImageController {
	private final ProjectImageService projectImageService;
	
	public ProjectImageController(ProjectImageService projectImageService) {
		this.projectImageService = projectImageService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectImageDto> create(@PathVariable String username, @PathVariable String projectName, @RequestBody ProjectImageCreateDto projectImageCreateDto, @AuthenticationPrincipal AuthUser user) {
		ProjectImageDto projectImageDto = projectImageService.create(username, projectName, projectImageCreateDto, user.email());
		return ResponseEntity.created(URI.create(
				UriUtil.getImageUri(
						UriUtil.toSlug(username),
						UriUtil.toSlug(projectImageDto.getProjectName()),
						projectImageDto.getId())))
				.body(projectImageDto);
	}
	
	@GetMapping
	public ResponseEntity<List<ProjectImageDto>> getImagesForProject(@PathVariable String username, @PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectImageService.getImagesForProject(username, UriUtil.fromSlug(projectName), user.email()));
	}
	
	@GetMapping(UriUtil.IMAGE)
	public ResponseEntity<ProjectImageDto> getImageForProject(@PathVariable String username, @PathVariable String projectName, @PathVariable Long imageId, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectImageService.getImageForProject(username, UriUtil.fromSlug(projectName), imageId, user.email()));
	}
	@PutMapping
	public ResponseEntity<ProjectImageDto> update(@PathVariable String username, @RequestBody ProjectImageUpdateDto projectImageUpdateDto, @PathVariable String projectName, @AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(projectImageService.updateProjectImage(username, projectImageUpdateDto, user.email()));
	}
	
	@DeleteMapping(UriUtil.IMAGE)
	public ResponseEntity<Void> delete(@PathVariable String username, @PathVariable String projectName, @PathVariable Long imageId, @AuthenticationPrincipal AuthUser user) {
		projectImageService.deleteProjectImage(username, imageId, user.email());
		return ResponseEntity.noContent().build();
	}
}
