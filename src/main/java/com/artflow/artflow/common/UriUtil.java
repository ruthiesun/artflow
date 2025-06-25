package com.artflow.artflow.common;

import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

public class UriUtil {
	public static String toSlug(String name) {
		return UriUtils.encodePathSegment(name, StandardCharsets.UTF_8);
	}
	
	public static String fromSlug(String slug) {
		return UriUtils.decode(slug, StandardCharsets.UTF_8);
	}
}
