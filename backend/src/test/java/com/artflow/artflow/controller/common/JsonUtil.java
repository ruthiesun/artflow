package com.artflow.artflow.controller.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtil {
	public static void checkMockResponse(ObjectMapper objectMapper, Set<Field> expectedFields, MvcResult res) throws UnsupportedEncodingException, JsonProcessingException {
		if (expectedFields.isEmpty()) {
			return;
		}
		List<String> fieldsToRecord = new ArrayList<>();
		for (Field field : expectedFields) {
			fieldsToRecord.add(field.name);
		}
		
		JsonNode node = objectMapper.readTree(res.getResponse().getContentAsString());
		Set<Field> actualFields = new HashSet<>();
		for (String field : fieldsToRecord) {
			actualFields.add(new Field(field, node.get(field).asText()));
		}
		assertEquals(expectedFields, actualFields);
	}
	
	public static void checkMockResponses(ObjectMapper objectMapper, Set<Set<Field>> expectedFields, MvcResult res) throws UnsupportedEncodingException, JsonProcessingException {
		Iterator<JsonNode> nodes = getNodesFromMockResponse(objectMapper, res);
		assertExpectedNodes(expectedFields, nodes);
	}
	
	private static Iterator<JsonNode> getNodesFromMockResponse(ObjectMapper objectMapper, MvcResult res)
			throws UnsupportedEncodingException, JsonProcessingException {
		String resBody = res.getResponse().getContentAsString();
		JsonNode resBodyJson = objectMapper.readTree(resBody);
		return resBodyJson.elements();
	}
	
	private static void assertExpectedNodes(Set<Set<Field>> expectedFields, Iterator<JsonNode> nodes) {
		if (expectedFields.isEmpty()) {
			return;
		}
		List<String> fieldsToRecord = new ArrayList<>();
		for (Field field : expectedFields.iterator().next()) {
			fieldsToRecord.add(field.name);
		}
		
		Set<Set<Field>> actualFields = new HashSet<>();
		while (nodes.hasNext()) {
			JsonNode currNode = nodes.next();
			Set<Field> currFieldSet = new HashSet<>();
			for (String field : fieldsToRecord) {
				currFieldSet.add(new Field(field, currNode.get(field).asText()));
			}
			actualFields.add(currFieldSet);
		}
		assertEquals(expectedFields, actualFields);
	}
	
	public static class Field {
		private String name;
		private String value;
		public Field (String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(name, value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Field)) return false;
			Field that = (Field) obj;
			return Objects.equals(name, that.name) &&
					Objects.equals(value, that.value);
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
	}
	
}

