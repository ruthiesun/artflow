package com.artflow.artflow.exception;

public class ProjectTagNotFoundException extends NotFoundException {
	public ProjectTagNotFoundException(String tagName, String projectName) {
		super("Project \"" + projectName + "\" doesn't have tag \"" + tagName + "\"");
	}
	
}
