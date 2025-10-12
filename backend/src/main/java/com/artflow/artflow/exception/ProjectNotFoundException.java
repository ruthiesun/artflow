package com.artflow.artflow.exception;

public class ProjectNotFoundException extends NotFoundException {
	public ProjectNotFoundException(String name, String username) {
		super("Project with name \"" + name + "\" under user \"" + username + "\" not found");
	}
}
