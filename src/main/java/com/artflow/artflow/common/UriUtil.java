package com.artflow.artflow.common;

import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

public class UriUtil {
	public static final String BASE = "/api";
	public static final String AUTH = "/auth";
	public static final String SIGNUP = "/signup";
	public static final String LOGIN = "/login";
	public static final String PROJECTS = "/projects";
	
	public static final String PROJECT = "/{projectName}";
	public static final String PUBLIC_PROJECTS = "/public";
	public static final String IMAGES = "/images";
	public static final String IMAGE = "/{imageId}";
	public static final String TAGS = "/tags";
	public static final String TAG = "/{tagName}";
	
	public static String toSlug(String name) {
		return UriUtils.encodePathSegment(name, StandardCharsets.UTF_8);
	}
	
	public static String fromSlug(String slug) {
		return UriUtils.decode(slug, StandardCharsets.UTF_8);
	}
	
	public static String getLoginUri() {
		return BASE + AUTH + LOGIN;
	}
	
	public static String getSignupUri() {
		return BASE + AUTH + SIGNUP;
	}
	
	public static String getProjectsUri() {
		return BASE + PROJECTS;
	}
	
	public static String getPublicProjectsUri() {
		return getProjectsUri() + PUBLIC_PROJECTS;
	}
	
	public static String getProjectUri(String name) {
		return getProjectsUri() + "/" + name;
	}
	
	public static String getImagesUri(String projectName) {
		return getProjectUri(projectName) + IMAGES;
	}
	
	public static String getImageUri(String projectName, Long imageId) {
		return getImagesUri(projectName) + "/" + imageId;
	}
	
	public static String getTagsUri() {
		return BASE + TAGS;
	}
	
	public static String getProjectTagsUri(String projectName) {
		return getTagsUri() + "/" + projectName;
	}
	
	public static String getProjectTagUri(String projectName, String tagName) {
		return getProjectTagsUri(projectName) + "/" + tagName;
	}
}
