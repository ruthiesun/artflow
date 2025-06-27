package com.artflow.artflow.exception;

public class ProjectTagNotFoundException extends NotFoundException {
	public ProjectTagNotFoundException(Long tagId, Long projectId) {
		super("Project with id \"" + projectId + "\" doesn't have tag with id \"" + tagId + "\"");
	}
	
	public ProjectTagNotFoundException(String tagName, String projectName) {
		super("Project \"" + projectName + "\" doesn't have tag \"" + tagName + "\"");
	}
	
}
