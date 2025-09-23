package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.ProjectTagDto;
import com.artflow.artflow.dto.TagDto;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.ProjectTagService;
import jakarta.validation.Valid;
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
@RequestMapping(UriUtil.BASE + UriUtil.USERNAME)
public class ProjectTagController {
	private final ProjectTagService projectTagService;
	
	public ProjectTagController(ProjectTagService projectTagService) {
		this.projectTagService = projectTagService;
	}
	
	@PostMapping(UriUtil.PROJECTS + UriUtil.PROJECT + UriUtil.TAGS)
	public ResponseEntity<ProjectTagDto> create(@PathVariable String username, @Valid @RequestBody ProjectTagCreateDto projectTagCreateDto,
												@AuthenticationPrincipal AuthUser user) {
		ProjectTagDto tagDto = projectTagService.create(username, projectTagCreateDto, user.id());
		return ResponseEntity
				.created(URI.create(UriUtil.getProjectTagUri(
						UriUtil.toSlug(username),
						UriUtil.toSlug(projectTagCreateDto.getProjectName()),
						UriUtil.toSlug(tagDto.getTagName()))))
				.body(tagDto);
	}
	
	@GetMapping(UriUtil.PROJECTS + UriUtil.PROJECT +  UriUtil.TAGS + UriUtil.TAG)
	public ResponseEntity<ProjectTagDto> getTagForProject(@PathVariable String username, @PathVariable String projectName,
														  @PathVariable String tagName, @AuthenticationPrincipal AuthUser user) {
		Long id = user == null ? null : user.id();
		return ResponseEntity.ok(projectTagService.getTagForProject(
				username,
				UriUtil.fromSlug(projectName),
				UriUtil.fromSlug(tagName),
				id));
	}
	
	@GetMapping(UriUtil.TAGS)
	public ResponseEntity<List<TagDto>> getTags(@PathVariable String username, @AuthenticationPrincipal AuthUser user) {
		Long id = user == null ? null : user.id();
		return ResponseEntity.ok(projectTagService.getTags(username, id));
	}
	
	@GetMapping(UriUtil.PROJECTS + UriUtil.PROJECT +  UriUtil.TAGS)
	public ResponseEntity<List<ProjectTagDto>> getTagsForProject(@PathVariable String username, @PathVariable String projectName,
																 @AuthenticationPrincipal AuthUser user) {
		Long id = user == null ? null : user.id();
		return ResponseEntity.ok(projectTagService.getTagsForProject(username, UriUtil.fromSlug(projectName), id));
	}
	
	@DeleteMapping(UriUtil.PROJECTS + UriUtil.PROJECT +  UriUtil.TAGS + UriUtil.TAG)
	public ResponseEntity<Void> delete(@PathVariable String username, @PathVariable String projectName,
									   @PathVariable String tagName, @AuthenticationPrincipal AuthUser user) {
		projectTagService.deleteTag(
				username,
				UriUtil.fromSlug(projectName),
				UriUtil.fromSlug(tagName),
				user.id());
		return ResponseEntity.noContent().build();
	}

}
