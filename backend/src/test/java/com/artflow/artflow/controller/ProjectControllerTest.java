package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.controller.common.JsonUtil;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectDto;
import com.artflow.artflow.dto.ProjectUpdateDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.dto.common.ValidationConstants;
import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.ProjectTagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.service.AuthService;
import com.artflow.artflow.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.persistence.EntityManager;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
	"jwt.auth-secret=test-secret-auth",
	"jwt.verify-secret=test-secret-verify"
})
public class ProjectControllerTest {
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private ProjectTagRepository projectTagRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ProjectService projectService;
	private User user;
	private String token;

	private String email = "ruthieismakinganapp@gmail.com";
	private String username = "test-username_";
	private String password = "testPassword1!";
	
	private String altEmail = "duthieismakinganapp@gmail.com";
	private String altUsername = "test-username_-";
	private String altPassword = "testPassword2!";

	@Test
	public void canCreateProject() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated())
			.andReturn();
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_UsernameIgnoreCaseAndProjectNameIgnoreCase(user.getUsername(), projectCreateDto.getProjectName());
		assertTrue(foundProject.isPresent());
		assertTrue(foundProject.get().getVisibility() == Visibility.PUBLIC);
		
		LocalDateTime createdDateTime = LocalDateTime.parse(objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("createdDateTime").asText());
		LocalDateTime updatedDateTime1 = LocalDateTime.parse(objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		assertEquals(createdDateTime, updatedDateTime1);
	}
	
	@Test
	public void canCreateProjectWithTags() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		projectCreateDto.setTagStrings(List.of("some tag", "some tag", "some other tag", "yet another tag"));
		
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated());
		
		
		List<ProjectTag> tagsFromProjectTagRepo = projectTagRepository.findByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(
				projectCreateDto.getProjectName(), user.getUsername());
		Set<String> expectedTags = new HashSet<>(projectCreateDto.getTagStrings());
		for (ProjectTag tag : tagsFromProjectTagRepo) {
			String tagString = tag.getTag().getName();
			assertTrue(expectedTags.contains(tagString));
			expectedTags.remove(tagString);
		}
		assertTrue(expectedTags.isEmpty());
		
		entityManager.clear();
		Optional<UserProject> foundProject = projectRepository.findByIdWithTags(tagsFromProjectTagRepo.get(0).getProject().getId());
		assertTrue(foundProject.isPresent());
		List<ProjectTag> tagsFromProjectReference = foundProject.get().getProjectTags();
		expectedTags = new HashSet<>(projectCreateDto.getTagStrings());
		for (ProjectTag tag : tagsFromProjectReference) {
			String tagString = tag.getTag().getName();
			assertTrue(expectedTags.contains(tagString));
			expectedTags.remove(tagString);
		}
		assertTrue(expectedTags.isEmpty());
	}
	
	@Test
	public void cannotCreateProjectWithInvalidName() throws Exception {
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectCreateDto("a project ", "desc", Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectCreateDto(" a project", "desc", Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectCreateDto("a  project", "desc", Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectCreateDto("a project?", "desc", Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotCreateProjectWithInvalidDescription() throws Exception {
        String longDesc = "a".repeat(ValidationConstants.PROJECT_DESC_LENGTH_MAX + 1);
		
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectCreateDto("a project", longDesc, Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotCreateProjectWithInvalidTags() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		
		projectCreateDto.setTagStrings(List.of("tag a ", "tag b"));
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isBadRequest());
		
		projectCreateDto.setTagStrings(List.of(" tag a", "tag b"));
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isBadRequest());
		
		projectCreateDto.setTagStrings(List.of("tag  a", "tag b"));
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotCreateProjectsWithSameName() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		
		ProjectCreateDto projectCreateDtoConflict = new ProjectCreateDto(projectCreateDto1.getProjectName(), "", Visibility.PUBLIC);
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDtoConflict)))
				.andExpect(status().isConflict());
		
		projectCreateDtoConflict = new ProjectCreateDto(projectCreateDto1.getProjectName().toUpperCase(), "", Visibility.PUBLIC);
		mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDtoConflict)))
			.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotCreateProjectForOtherUser() throws Exception {
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		mockMvc.perform(post(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canGetAllProjects() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName()))),
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto2.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	@Test
	public void canGetAllZeroProjects() throws Exception {
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		JsonUtil.checkMockResponses(objectMapper, new HashSet<>(), res);
	}
	
	@Test
	public void canGetAllPublicProjects() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUriWithQueryParams(user.getUsername(), null, Visibility.PUBLIC))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	@Test
	public void canGetAllPublicProjectsWithOtherUser() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword, true));
		
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(anotherUser.getUsername(), projectCreateDto1, anotherUser.getId());
		projectService.create(anotherUser.getUsername(), projectCreateDto2, anotherUser.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
			new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	@Test
	public void canGetAllPublicProjectsWithoutLogin() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(user.getUsername())))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
			new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	@Test
	public void canGetAllProjectsWithTags() throws Exception {
		String tag1 = "some tag 1";
		String tag2 = "some tag 2";
		String tag3 = "some tag 3";
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		projectCreateDto1.setTagStrings(List.of(tag1, tag2, tag3));
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectCreateDto2.setTagStrings(List.of(tag1, tag3));
		ProjectCreateDto projectCreateDto3 = new ProjectCreateDto("proj 3", null, Visibility.PUBLIC);
		projectCreateDto3.setTagStrings(List.of(tag3));
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());
		projectService.create(user.getUsername(), projectCreateDto3, user.getId());
		
		ProjectCreateDto projectCreateDto4 = new ProjectCreateDto("proj 4", null, Visibility.PUBLIC);
		projectCreateDto4.setTagStrings(List.of(tag1, tag2, tag3));
		projectService.create(anotherUser.getUsername(), projectCreateDto4, anotherUser.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUriWithQueryParams(user.getUsername(), List.of(tag1, tag2), null))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName()))),
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto2.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	// testing that:
	// other user's projects are filtered out
	// only the desired visibility is included
	// if the project contains at least one of the tags, it's included (union)
	@Test
	public void canGetAllProjectsWithQuery() throws Exception {
		String tag1 = "some tag 1";
		String tag2 = "some tag 2";
		String tag3 = "some tag 3";
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		projectCreateDto1.setTagStrings(List.of(tag1, tag2, tag3));
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		projectCreateDto2.setTagStrings(List.of(tag1, tag3));
		ProjectCreateDto projectCreateDto3 = new ProjectCreateDto("proj 3", null, Visibility.PUBLIC);
		projectCreateDto3.setTagStrings(List.of(tag3));
		projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());
		projectService.create(user.getUsername(), projectCreateDto3, user.getId());
		
		ProjectCreateDto projectCreateDto4 = new ProjectCreateDto("proj 4", null, Visibility.PUBLIC);
		projectCreateDto4.setTagStrings(List.of(tag1, tag2, tag3));
		projectService.create(anotherUser.getUsername(), projectCreateDto4, anotherUser.getId());
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUriWithQueryParams(user.getUsername(), List.of(tag1, tag2), Visibility.PUBLIC))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
	}
	
	@Test
	public void cannotGetProjectsWithInvalidVisibility() throws Exception {
		mockMvc.perform(get(UriUtil.getProjectsUri(user.getUsername()) + "?visibility=bad")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotGetPrivateProjectsForOtherUser() throws Exception {
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", "desc", Visibility.PRIVATE);
		projectService.create(anotherUser.getUsername(), projectCreateDto1, anotherUser.getId());
		projectService.create(anotherUser.getUsername(), projectCreateDto2, anotherUser.getId());
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
			new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
		
		res = mockMvc.perform(get(UriUtil.getProjectsUriWithQueryParams(anotherUser.getUsername(), null, Visibility.PUBLIC))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
		
		mockMvc.perform(get(UriUtil.getProjectsUriWithQueryParams(anotherUser.getUsername(), null, Visibility.PRIVATE))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canGetProject() throws Exception {
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", null, Visibility.PRIVATE);
		ProjectDto projectDto1 = projectService.create(user.getUsername(), projectCreateDto1, user.getId());
		projectService.create(user.getUsername(), projectCreateDto2, user.getId());

		MvcResult res = mockMvc.perform(get(UriUtil.getProjectUri(user.getUsername(), projectDto1.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<JsonUtil.Field> expectedProject = new HashSet<>(
				List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName()))
		);
		JsonUtil.checkMockResponse(objectMapper, expectedProject, res);
	}
	
	@Test
	public void cannotGetProjectThatDoesNotExist() throws Exception {
		mockMvc.perform(get(UriUtil.getProjectUri(user.getUsername(), "adgsfd"))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void cannotGetPrivateProjectForOtherUser() throws Exception {
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", "desc", Visibility.PRIVATE);
		projectService.create(anotherUser.getUsername(), projectCreateDto1, anotherUser.getId());
		projectService.create(anotherUser.getUsername(), projectCreateDto2, anotherUser.getId());
		
		Set<Set<JsonUtil.Field>> expectedProjects = new HashSet<>(List.of(
			new HashSet<>(List.of(new JsonUtil.Field("projectName", projectCreateDto1.getProjectName())))
		));
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		JsonUtil.checkMockResponses(objectMapper, expectedProjects, res);
		
		mockMvc.perform(get(UriUtil.getProjectUri(anotherUser.getUsername(), projectCreateDto2.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canUpdateProject() throws Exception {
		String name1 = "sdf";
		String name2 = "asdf";
		String desc1 = "desc";
		String desc2 = "desc!";
		Visibility visibility1 = Visibility.PUBLIC;
		Visibility visibility2 = Visibility.PRIVATE;
		
		ProjectCreateDto projectCreateDto = new ProjectCreateDto(name1, desc1, visibility1);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectDto.getId(), name2, desc2, visibility2);
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
				.andExpect(status().isOk());
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_UsernameIgnoreCaseAndProjectNameIgnoreCase(user.getUsername(), projectUpdateDto.getProjectName());
		assertTrue(foundProject.isPresent());
		assertSame(visibility2, foundProject.get().getVisibility());
		assertEquals(name2, foundProject.get().getProjectName());
		assertEquals(desc2, foundProject.get().getDescription());
		assertTrue(foundProject.get().getUpdatedDateTime().isAfter(projectDto.getUpdatedDateTime()));
	}
	
	@Test
	public void cannotUpdateProjectWithInvalidName() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(
					new ProjectUpdateDto(projectDto.getId(), "a project ", projectDto.getDescription(), projectDto.getVisibility()))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(
					new ProjectUpdateDto(projectDto.getId()," a project", projectDto.getDescription(), projectDto.getVisibility()))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(
					new ProjectUpdateDto(projectDto.getId(),"a  project", projectDto.getDescription(), projectDto.getVisibility()))))
			.andExpect(status().isBadRequest());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(
					new ProjectUpdateDto(projectDto.getId(),"a project?", projectDto.getDescription(), projectDto.getVisibility()))))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotUpdateProjectWithInvalidDescription() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		
		String longDesc = "a".repeat(ValidationConstants.PROJECT_DESC_LENGTH_MAX + 1);
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(
					new ProjectUpdateDto(projectDto.getId(),"a project", longDesc, Visibility.PUBLIC))))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotUpdateProjectWithInvalidTags() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectDto.getId(), projectDto.getProjectName(), projectDto.getDescription(), projectDto.getVisibility());
		projectUpdateDto.setTagStrings(List.of("tag a ", "tag b"));
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
			.andExpect(status().isBadRequest());
		
		projectCreateDto.setTagStrings(List.of(" tag a", "tag b"));
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
			.andExpect(status().isBadRequest());
		
		projectCreateDto.setTagStrings(List.of("tag  a", "tag b"));
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
			.andExpect(status().isBadRequest());
		
		projectCreateDto.setTagStrings(List.of("Tag a", "tag b"));
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotUpdateProjectThatDoesNotExist() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", Visibility.PRIVATE);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectDto.getId() + 1, projectDto.getProjectName(), projectDto.getDescription(), projectDto.getVisibility());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void cannotUpdateProjectForOtherUser() throws Exception {
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", "desc", Visibility.PRIVATE);
		ProjectDto projectDto1 = projectService.create(anotherUser.getUsername(), projectCreateDto1, anotherUser.getId());
		ProjectDto projectDto2 = projectService.create(anotherUser.getUsername(), projectCreateDto2, anotherUser.getId());
		
		ProjectUpdateDto projectUpdateDto1 = new ProjectUpdateDto(projectDto1.getId(), projectDto1.getProjectName() + "test", projectDto1.getDescription(), projectDto1.getVisibility());
		ProjectUpdateDto projectUpdateDto2 = new ProjectUpdateDto(projectDto1.getId(), projectDto2.getProjectName() + "test", projectDto2.getDescription(), projectDto2.getVisibility());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto1)))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put(UriUtil.getProjectsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectUpdateDto2)))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canDeleteProject() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("proj", "desc", Visibility.PRIVATE);
		ProjectDto projectDto = projectService.create(user.getUsername(), projectCreateDto, user.getId());
		
		mockMvc.perform(delete(UriUtil.getProjectUri(user.getUsername(), projectDto.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		Optional<UserProject> foundProject = projectRepository.findByOwner_UsernameIgnoreCaseAndProjectNameIgnoreCase(user.getUsername(), projectCreateDto.getProjectName());
		assertTrue(foundProject.isEmpty());
	}
	
	@Test
	public void canDeleteProjectThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getProjectUri(user.getUsername(), "sfdgfd"))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void cannotDeleteProjectForOtherUser() throws Exception {
		User anotherUser = new User(altEmail, altUsername, altPassword);
		userRepository.save(anotherUser);
		ProjectCreateDto projectCreateDto1 = new ProjectCreateDto("proj 1", "desc", Visibility.PUBLIC);
		ProjectCreateDto projectCreateDto2 = new ProjectCreateDto("proj 2", "desc", Visibility.PRIVATE);
		ProjectDto projectDto1 = projectService.create(anotherUser.getUsername(), projectCreateDto1, anotherUser.getId());
		ProjectDto projectDto2 = projectService.create(anotherUser.getUsername(), projectCreateDto2, anotherUser.getId());
		
		mockMvc.perform(delete(UriUtil.getProjectUri(anotherUser.getUsername(), projectDto1.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(delete(UriUtil.getProjectUri(anotherUser.getUsername(), projectDto2.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canLoginAndManageProjects() throws Exception {
		// login and get token
		LoginDto loginDto = new LoginDto(email, password);
		MvcResult loginResult = mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isOk())
				.andReturn();
		
		String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
		
		// create a project with some tags
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		projectCreateDto.setTagStrings(List.of("a tag", "another tag"));
		MvcResult createResult = mockMvc.perform(post(UriUtil.getProjectsUri(username))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		MvcResult projectTagsResult = mockMvc.perform(get(UriUtil.getProjectTagsUri(username, projectCreateDto.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();

		Set<Set<JsonUtil.Field>> expectedTags = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("tagName", projectCreateDto.getTagStrings().get(0)))),
				new HashSet<>(List.of(new JsonUtil.Field("tagName", projectCreateDto.getTagStrings().get(1))))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedTags, projectTagsResult);
		
		Long projectId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();
		String projectName = "a new project name";
		String description = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("description").asText();
		Visibility visibility = Visibility.valueOf(objectMapper.readTree(createResult.getResponse().getContentAsString()).get("visibility").asText());
		
		// update the project with another tag
		ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(projectId, projectName, description, visibility);
		projectUpdateDto.setTagStrings(new ArrayList<>());
		projectUpdateDto.getTagStrings().add(projectCreateDto.getTagStrings().get(0));
		projectUpdateDto.getTagStrings().add("yet another");
		projectUpdateDto.getTagStrings().add("and another");
		MvcResult updateResult = mockMvc.perform(put(UriUtil.getProjectsUri(username))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectUpdateDto)))
				.andExpect(status().isOk())
				.andReturn();

		projectTagsResult = mockMvc.perform(get(UriUtil.getProjectTagsUri(username, projectUpdateDto.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();

		expectedTags = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("tagName", projectUpdateDto.getTagStrings().get(0)))),
				new HashSet<>(List.of(new JsonUtil.Field("tagName", projectUpdateDto.getTagStrings().get(1)))),
				new HashSet<>(List.of(new JsonUtil.Field("tagName", projectUpdateDto.getTagStrings().get(2))))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedTags, projectTagsResult);
		
		projectName = objectMapper.readTree(updateResult.getResponse().getContentAsString()).get("projectName").asText();
		
		// check that the project still exists
		mockMvc.perform(get(UriUtil.getProjectUri(username, projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk());
		
		// delete the project
		mockMvc.perform(delete(UriUtil.getProjectUri(username, projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		// check that the associated tags are deleted when the refcount reaches 0
		MvcResult tagsResult = mockMvc.perform(get(UriUtil.getTagsUri(username))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();

		JsonUtil.checkMockResponses(objectMapper, new HashSet<>(), tagsResult);
		
		// and check that the project itself has been deleted
		mockMvc.perform(get(UriUtil.getProjectUri(username, projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@BeforeEach
	public void setup() throws FirebaseAuthException {
		authService.register(new SignupDto(email, username, password));
		user = userRepository.findByEmailIgnoreCase(email).get();
		user.setIsVerified(true);
		token = authService.login(new LoginDto(email, password)).getToken();
	}
}

