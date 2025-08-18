package com.artflow.artflow.exception;

public class ProjectNotFoundException extends NotFoundException {
	public ProjectNotFoundException(String name, String username) {
		super("Project with name \"" + name + "\" under user \"" + username + "\" not found");
	}
	
	public ProjectNotFoundException(Long id, String username) {
		super("Project with id \"" + id + "\" under user \"" + username + "\" not found");
	}
}
