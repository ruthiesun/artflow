package com.artflow.artflow.exception;

public class ProjectNotFoundException extends NotFoundException {
	public ProjectNotFoundException(String name, String email) {
		super("Project with name " + name + " under user " + email + " not found");
	}
	
	public ProjectNotFoundException(Long id, String email) {
		super("Project with id " + id + " under user " + email + " not found");
	}
}
