package com.artflow.artflow.exception;

public class ProjectImageNotFoundException extends NotFoundException {
	public ProjectImageNotFoundException(long id) {
		super("Image with id " + id + " not found");
	}
}
