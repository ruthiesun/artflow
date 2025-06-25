package com.artflow.artflow.exception;

public class ProjectTagNameInUseException extends InUseException {
	public ProjectTagNameInUseException(String tagName, Long projectId) {
		super("Tag name " + tagName + " exists for project with id " + projectId);
	}
	
	public ProjectTagNameInUseException(String tagName, String projectName) {
		super("Tag name " + tagName + " exists for project " + projectName);
	}
	
}
