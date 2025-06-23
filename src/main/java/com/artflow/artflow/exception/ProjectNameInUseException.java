package com.artflow.artflow.exception;

public class ProjectNameInUseException extends InUseException{
	public ProjectNameInUseException(String name) {
		super("Project name " + name + " is taken");
	}
}
