package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.service.AuthService;
import com.artflow.artflow.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
		"jwt.signing-secret=test-secret"
})
public class ProjectControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ProjectService projectService;
	private User user;
	private String token;

	@Test
	public void canCreateProject() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", Visibility.PUBLIC);
		
		mockMvc.perform(post("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated());
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_EmailAndProjectName(user.getEmail(), projectCreateDto.getProjectName());
		assertTrue(foundProject.isPresent());
	}
	
	@Test
	public void cannotCreateProjectsWithSameName() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj", "desc", Visibility.PUBLIC);
		projectService.create(projectCreateDto1, user.getEmail());
		
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto();
		projectCreateDto2.setProjectName(projectCreateDto1.getProjectName());
		mockMvc.perform(post("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto2)))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void canGetAllProjects() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(projectCreateDto1, user.getEmail());
		projectService.create(projectCreateDto2, user.getEmail());
		Set<String> expectedProjects = new HashSet<>(List.of(projectCreateDto1.getProjectName(),
				projectCreateDto2.getProjectName()));
		
		MvcResult res = mockMvc.perform(get("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		checkProjectsMockResponse(res, expectedProjects);
	}
	
	@Test
	public void canGetAllZeroProjects() throws Exception {
		Set<String> expectedProjects = new HashSet<>();
		
		MvcResult res = mockMvc.perform(get("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		checkProjectsMockResponse(res, expectedProjects);
	}
	
	@Test
	public void canGetAllPublicProjects() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(projectCreateDto1, user.getEmail());
		projectService.create(projectCreateDto2, user.getEmail());
		Set<String> expectedProjects = new HashSet<>(List.of(projectCreateDto1.getProjectName()));
		
		MvcResult res = mockMvc.perform(get("/api/projects/public")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		checkProjectsMockResponse(res, expectedProjects);
	}
	
	@Test
	public void canGetProject() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		ProjectDto projectDto1 = projectService.create(projectCreateDto1, user.getEmail());
		projectService.create(projectCreateDto2, user.getEmail());
		
		MvcResult res = mockMvc.perform(get("/api/projects/" + projectDto1.getId())
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		checkProjectMockResponse(res, projectCreateDto1.getProjectName());
	}
	
	@Test
	public void cannotGetProjectThatDoesNotExist() throws Exception {
		Long id = 0L;

		mockMvc.perform(get("/api/projects/" + id)
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void canUpdateProject() throws Exception {
		Visibility visibility1 = Visibility.PUBLIC;
		Visibility visibility2 = Visibility.PRIVATE;
		
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", visibility1);
		ProjectDto projectDto = projectService.create(projectCreateDto, user.getEmail());
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectDto.getId(), projectDto.getProjectName(), projectDto.getDescription(), visibility2);
		
		mockMvc.perform(put("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
				.andExpect(status().isOk());
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_EmailAndProjectName(user.getEmail(), projectDto.getProjectName());
		assertTrue(foundProject.isPresent());
		assertSame(visibility2, foundProject.get().getVisibility());
	}
	
	@Test
	public void cannotUpdateProjectThatDoesNotExist() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", Visibility.PRIVATE);
		ProjectDto projectDto = projectService.create(projectCreateDto, user.getEmail());
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectDto.getId() + 1, projectDto.getProjectName(), projectDto.getDescription(), projectDto.getVisibility());
		
		mockMvc.perform(put("/api/projects")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void canDeleteProject() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", Visibility.PRIVATE);
		ProjectDto projectDto = projectService.create(projectCreateDto, user.getEmail());
		
		mockMvc.perform(delete("/api/projects/" + projectDto.getId())
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_EmailAndProjectName(user.getEmail(), projectCreateDto.getProjectName());
		assertTrue(foundProject.isEmpty());
	}
	
	@Test
	public void canDeleteProjectThatDoesNotExist() throws Exception {
		Long id = 0L;
		
		mockMvc.perform(delete("/api/projects/" + id)
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@BeforeEach
	public void setup() {
		user = new User("testemail", "testpassword");
		token = authService.register(new SignupDto(user.getEmail(), user.getPassword())).getToken();
	}
	
	private void checkProjectMockResponse(MvcResult res, String expectedProjectName) throws UnsupportedEncodingException, JsonProcessingException {
		JsonNode projectNodeJson = objectMapper.readTree(res.getResponse().getContentAsString());
		assertEquals(expectedProjectName, projectNodeJson.get("projectName").asText());
	}
	
	private void checkProjectsMockResponse(MvcResult res, Set<String> expectedProjectNames) throws UnsupportedEncodingException, JsonProcessingException {
		Iterator<JsonNode> projectNodeJsons = getProjectNodesFromMockResponse(res);
		assertExpectedProjectNodes(expectedProjectNames, projectNodeJsons);
	}
	
	private Iterator<JsonNode> getProjectNodesFromMockResponse(MvcResult res)
			throws UnsupportedEncodingException, JsonProcessingException {
		String resBody = res.getResponse().getContentAsString();
		JsonNode resBodyJson = objectMapper.readTree(resBody);
		return resBodyJson.elements();
	}
	
	private void assertExpectedProjectNodes(Set<String> expectedProjectNames, Iterator<JsonNode> projectNodeJsons) {
		while (projectNodeJsons.hasNext()) {
			JsonNode currElem = projectNodeJsons.next();
			String currElemName = currElem.get("projectName").asText();
			assertTrue(expectedProjectNames.contains(currElemName));
			expectedProjectNames.remove(currElemName);
		}
		assertTrue(expectedProjectNames.isEmpty());
	}
}

