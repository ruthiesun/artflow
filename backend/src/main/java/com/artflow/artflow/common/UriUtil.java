package com.artflow.artflow.common;

import com.artflow.artflow.model.Visibility;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class UriUtil {
	public static final String BASE = "/api";
	public static final String AUTH = "/auth";
	public static final String SIGNUP = "/register";
	public static final String LOGIN = "/login";
	public static final String LOGOUT = "/logout";
	public static final String VERIFY = "/verify";
	public static final String USERNAME = "/{username}";
	
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
	
	public static String getLogoutUri() {
		return BASE + AUTH + LOGOUT;
	}
	
	public static String getSignupUri() {
		return BASE + AUTH + SIGNUP;
	}
	
	public static String getVerifyUri() {
		return BASE + AUTH + VERIFY;
	}
	
	public static String getProjectsUriWithQueryParams(String username, List<String> tags, Visibility visibility) {
		StringBuilder query = new StringBuilder();
		if (tags != null && !tags.isEmpty()) {
			query.append("?tags=");
			for (int i = 0; i < tags.size() - 1; i++) {
				query.append(tags.get(i)).append(",");
			}
			query.append(tags.get(tags.size() - 1));
		}
		if (visibility != null) {
			if (query.isEmpty()) {
				query.append("?");
			}
			else {
				query.append("&");
			}
			query.append("visibility=").append(visibility);
		}
		return getProjectsUri(username) + query;
	}
	
	public static String getProjectsUri(String username) {
		return BASE + "/" + username + PROJECTS;
	}
	
	public static String getProjectUri(String username, String name) {
		return getProjectsUri(username) + "/" + name;
	}
	
	public static String getImagesUri(String username, String projectName) {
		return getProjectUri(username, projectName) + IMAGES;
	}
	
	public static String getImageUri(String username, String projectName, Long imageId) {
		return getImagesUri(username, projectName) + "/" + imageId;
	}
	
	public static String getTagsUri(String username) {
		return BASE + "/" + username + "/" + TAGS;
	}
	
	public static String getProjectTagsUri(String username, String projectName) {
		return getProjectUri(username, projectName) + TAGS;
	}
	
	public static String getProjectTagUri(String username, String projectName, String tagName) {
		return getProjectTagsUri(username, projectName) + "/" + tagName;
	}
	
	public static String getAuthWildcardUri() {
		return BASE + AUTH + "/**";
	}
	
	public static String getProjectsWildcardUri() {
		return BASE + "/*" + PROJECTS;
	}
	
	public static String getProjectWildcardUri() {
		return BASE + "/*" + PROJECTS + "/*";
	}
	
	public static String getProjectImagesWildcardUri() {
		return BASE + "/*" + PROJECTS + "/*" + IMAGES;
	}
	
	public static String getProjectImageWildcardUri() {
		return BASE + "/*" + PROJECTS + "/*" + IMAGES + "/*";
	}
	
	public static String getProjectTagsWildcardUri() {
		return BASE + "/*" + PROJECTS + "/*" + TAGS;
	}
	
	public static String getProjectTagWildcardUri() {
		return BASE + "/*" + PROJECTS + "/*" + TAGS + "/*";
	}
	
	public static String getTagsWildcardUri() {
		return BASE + "/*" + TAGS;
	}
}
