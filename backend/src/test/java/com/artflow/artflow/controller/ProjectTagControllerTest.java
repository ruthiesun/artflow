package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.controller.common.JsonUtil;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectTagCreateDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.ProjectTagRepository;
import com.artflow.artflow.repository.TagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.service.AuthService;
import com.artflow.artflow.validation.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
	"jwt.auth-secret=test-secret-auth",
	"jwt.verify-secret=test-secret-verify"
})
public class ProjectTagControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private ProjectTagRepository projectTagRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ValidationService validationService;
	
	private User user;
	private UserProject project;
	private Tag tag1;
	private Tag tag2;
	private String token;
	
	private String email = "ruthieismakinganapp@gmail.com";
	private String username = "test-username_";
	private String password = "testPassword1!";
	
	private String altEmail = "duthieismakinganapp@gmail.com";
	private String altUsername = "test-username_-";
	private String altPassword = "testPassword2!";
	
	@Test
	public void canCreateProjectTagExistingTag() throws Exception {
		String projectName = project.getProjectName();
		String tagName = tag1.getName();
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated());
		
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(
				tagName, projectName, project.getOwner().getUsername());
		assertTrue(foundProjectTag.isPresent());
		List<String> tags = projectTagRepository.findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCase(user.getUsername());
        assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0));
	}
	
	@Test
	public void canCreateProjectTagNotExistingTag() throws Exception {
		String projectName = project.getProjectName();
		String tagName = "some tag";
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated());
		
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(
				tagName, projectName, project.getOwner().getUsername());
		assertTrue(foundProjectTag.isPresent());
	}
	
	@Test
	public void canCreateProjectTagHandleUpperCase() throws Exception {
		String projectName = project.getProjectName();
		String tagName = "A GREAT TAG";
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isCreated());
		
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(
			tagName, projectName, project.getOwner().getUsername());
		assertTrue(foundProjectTag.isPresent());
		List<String> tags = projectTagRepository.findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCase(user.getUsername());
		assertEquals(1, tags.size());
		assertEquals(tagName.toLowerCase(), tags.get(0));
	}
	
	@Test
	public void cannotCreateProjectTagWithInvalidTagName() throws Exception {
		String projectName = project.getProjectName();
		String tagName = "some tag ";
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
		
		tagName = " some tag";
		projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
		
		tagName = "some  tag";
		projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
		
		tagName = "tag!";
		projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
		
		tagName = "a".repeat(validationService.getRule("tag").getMinLength() - 1);
		projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
		
		tagName = "a".repeat(validationService.getRule("tag").getMaxLength() + 1);
		projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotCreateProjectTagTwice() throws Exception {
		String projectName = project.getProjectName();
		String tagName = tag1.getName();
		
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotCreateProjectTagForOtherUser() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword));
		UserProject publicProject = new UserProject(anotherUser, "public project");
		publicProject.setVisibility(Visibility.PUBLIC);
		UserProject privateProject = new UserProject(anotherUser, "private project");
		privateProject.setVisibility(Visibility.PRIVATE);
		projectRepository.save(publicProject);
		projectRepository.save(privateProject);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), publicProject.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectTagCreateDto(publicProject.getProjectName(), "alien"))))
			.andExpect(status().isNotFound());
		mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), privateProject.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new ProjectTagCreateDto(privateProject.getProjectName(), "alien"))))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canGetAllDistinctProjectTagNames() throws Exception {
		UserProject otherProject = projectRepository.save(new UserProject(user, "another test project"));
		
		ProjectTag projectTag1 = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag1.setProject(project);
		projectTag1.setTag(tag1);
		
		ProjectTag projectTag2 = new ProjectTag(new ProjectTagId(project.getId(), tag2.getId()));
		projectTag2.setProject(project);
		projectTag2.setTag(tag2);
		
		ProjectTag projectTag3 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag1.getId()));
		projectTag3.setProject(otherProject);
		projectTag3.setTag(tag1);
		
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		projectTagRepository.save(projectTag3);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getTagsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expected = new HashSet<>(List.of(
				new HashSet<>(List.of(
						new JsonUtil.Field("tagName", tag1.getName()))),
				new HashSet<>(List.of(
						new JsonUtil.Field("tagName", tag2.getName())))
				));
		JsonUtil.checkMockResponses(objectMapper, expected, res);
	}
	
	@Test
	public void canGetAllZeroProjectTags() throws Exception {
		MvcResult res = mockMvc.perform(get(UriUtil.getTagsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		JsonUtil.checkMockResponse(objectMapper, new HashSet<>(), res);
	}
	
	@Test
	public void canGetProjectTagForProject() throws Exception {
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagUri(user.getUsername(), project.getProjectName(), tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<JsonUtil.Field> expected = new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag1.getName()),
				new JsonUtil.Field("projectName", project.getProjectName()))
		);
		JsonUtil.checkMockResponse(objectMapper, expected, res);
	}
	
	@Test
	public void canGetProjectTagsForProject() throws Exception {
		ProjectTag projectTag1 = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag1.setProject(project);
		projectTag1.setTag(tag1);
		ProjectTag projectTag2 = new ProjectTag(new ProjectTagId(project.getId(), tag2.getId()));
		projectTag2.setProject(project);
		projectTag2.setTag(tag2);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		
		UserProject otherProject = projectRepository.save(new UserProject(user, "another test project"));
		ProjectTag projectTag3 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag1.getId()));
		projectTag3.setProject(otherProject);
		projectTag3.setTag(tag1);
		projectTagRepository.save(projectTag3);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagsUri(user.getUsername(), project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expected = new HashSet<>(List.of(
				new HashSet<>(List.of(
						new JsonUtil.Field("tagName", tag1.getName()),
						new JsonUtil.Field("projectName", project.getProjectName()))),
				new HashSet<>(List.of(
						new JsonUtil.Field("tagName", tag2.getName()),
						new JsonUtil.Field("projectName", project.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expected, res);
	}
	
	@Test
	public void canGetProjectTagsForPublicProjectWithOtherUser() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword, true));
		UserProject otherProject = new UserProject(anotherUser, "other project");
		otherProject.setVisibility(Visibility.PUBLIC);
		projectRepository.save(otherProject);
		
		ProjectTag projectTag1 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag1.getId()));
		projectTag1.setProject(otherProject);
		projectTag1.setTag(tag1);
		ProjectTag projectTag2 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag2.getId()));
		projectTag2.setProject(otherProject);
		projectTag2.setTag(tag2);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagsUri(anotherUser.getUsername(), otherProject.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expected = new HashSet<>(List.of(
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag1.getName()),
				new JsonUtil.Field("projectName", otherProject.getProjectName()))),
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag2.getName()),
				new JsonUtil.Field("projectName", otherProject.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expected, res);
	}
	
	@Test
	public void canGetProjectTagsForPublicProjectWithoutLogin() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword, true));
		UserProject otherProject = new UserProject(anotherUser, "other project");
		otherProject.setVisibility(Visibility.PUBLIC);
		projectRepository.save(otherProject);
		
		ProjectTag projectTag1 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag1.getId()));
		projectTag1.setProject(otherProject);
		projectTag1.setTag(tag1);
		ProjectTag projectTag2 = new ProjectTag(new ProjectTagId(otherProject.getId(), tag2.getId()));
		projectTag2.setProject(otherProject);
		projectTag2.setTag(tag2);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagsUri(anotherUser.getUsername(), otherProject.getProjectName())))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expected = new HashSet<>(List.of(
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag1.getName()),
				new JsonUtil.Field("projectName", otherProject.getProjectName()))),
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag2.getName()),
				new JsonUtil.Field("projectName", otherProject.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expected, res);
	}
	
	@Test
	public void cannotGetProjectTagInPrivateProjectForOtherUser() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword));
		UserProject publicProject = new UserProject(anotherUser, "public project");
		publicProject.setVisibility(Visibility.PUBLIC);
		UserProject privateProject = new UserProject(anotherUser, "private project");
		privateProject.setVisibility(Visibility.PRIVATE);
		publicProject = projectRepository.save(publicProject);
		privateProject = projectRepository.save(privateProject);
		ProjectTag projectTagPublic = new ProjectTag(new ProjectTagId(publicProject.getId(), tag1.getId()));
		projectTagPublic.setProject(publicProject);
		projectTagPublic.setTag(tag1);
		ProjectTag projectTagPrivate = new ProjectTag(new ProjectTagId(privateProject.getId(), tag2.getId()));
		projectTagPrivate.setProject(privateProject);
		projectTagPrivate.setTag(tag2);
		projectTagRepository.save(projectTagPublic);
		projectTagRepository.save(projectTagPrivate);
		
		
		
		// check tags
		MvcResult res = mockMvc.perform(get(UriUtil.getTagsUri(anotherUser.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedTags = new HashSet<>(List.of(
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag1.getName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedTags, res);
		
		// check project tags
		res = mockMvc.perform(get(UriUtil.getProjectTagsUri(anotherUser.getUsername(), publicProject.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		Set<Set<JsonUtil.Field>> expectedProjectTags = new HashSet<>(List.of(
			new HashSet<>(List.of(
				new JsonUtil.Field("tagName", tag1.getName()),
				new JsonUtil.Field("projectName", publicProject.getProjectName())))
		));
		JsonUtil.checkMockResponses(objectMapper, expectedProjectTags, res);
		
		mockMvc.perform(get(UriUtil.getProjectTagsUri(anotherUser.getUsername(), privateProject.getProjectName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canDeleteProjectTag() throws Exception {
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);
		
		mockMvc.perform(delete(UriUtil.getProjectTagUri(user.getUsername(), project.getProjectName(), tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		assertEquals(0L, projectTagRepository.count());
	}
	
	@Test
	public void canDeleteProjectTagThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getProjectTagUri(user.getUsername(), project.getProjectName(), tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void canDeleteProjectTagForProjectThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getProjectTagUri(user.getUsername(), "asdf", tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void cannotDeleteProjectTagForOtherUser() throws Exception {
		User anotherUser = userRepository.save(new User(altEmail, altUsername, altPassword));
		UserProject publicProject = new UserProject(anotherUser, "public project");
		publicProject.setVisibility(Visibility.PUBLIC);
		UserProject privateProject = new UserProject(anotherUser, "private project");
		privateProject.setVisibility(Visibility.PRIVATE);
		publicProject = projectRepository.save(publicProject);
		privateProject = projectRepository.save(privateProject);
		ProjectTag projectTagPublic = new ProjectTag(new ProjectTagId(publicProject.getId(), tag1.getId()));
		projectTagPublic.setTag(tag1);
		projectTagPublic.setProject(publicProject);
		ProjectTag projectTagPrivate = new ProjectTag(new ProjectTagId(privateProject.getId(), tag2.getId()));
		projectTagPrivate.setTag(tag2);
		projectTagPrivate.setProject(privateProject);
		projectTagRepository.save(projectTagPublic);
		projectTagRepository.save(projectTagPrivate);
		
		mockMvc.perform(delete(UriUtil.getProjectTagUri(anotherUser.getUsername(), publicProject.getProjectName(), tag1.getName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
		mockMvc.perform(delete(UriUtil.getProjectTagUri(anotherUser.getUsername(), publicProject.getProjectName(), tag2.getName()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void canUpdateProjectTimestampWithTag() throws Exception {
		// create project
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isCreated())
			.andReturn();
		
		String projectName = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("projectName").asText();
		LocalDateTime updatedDateTime1 = LocalDateTime.parse(objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		
		// create tag
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, "an excellent tag");
		MvcResult tagCreateResult = mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isCreated())
			.andReturn();
		
		String tagName = objectMapper.readTree(tagCreateResult.getResponse().getContentAsString()).get("tagName").asText();
		
		// check update timestamp is different
		MvcResult projectGetResult = mockMvc.perform(get(UriUtil.getProjectUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		LocalDateTime updatedDateTime2 = LocalDateTime.parse(objectMapper.readTree(projectGetResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		assertTrue(updatedDateTime2.isAfter(updatedDateTime1));
		
		// delete tag
		mockMvc.perform(delete(UriUtil.getProjectTagUri(user.getUsername(), projectName, tagName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNoContent());
		
		// check update timestamp is different
		projectGetResult = mockMvc.perform(get(UriUtil.getProjectUri(user.getUsername(), projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		LocalDateTime updatedDateTime3 = LocalDateTime.parse(objectMapper.readTree(projectGetResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		assertTrue(updatedDateTime3.isAfter(updatedDateTime2));
	}
	
	@Test
	public void canCreateProjectAndManageTags() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri(user.getUsername()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		Long projectId = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("id").asLong();
		String projectName = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("projectName").asText();
		String description = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("description").asText();
		Visibility visibility = Visibility.valueOf(objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("visibility").asText());
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, "an excellent tag");
		MvcResult createResult = mockMvc.perform(post(UriUtil.getProjectTagsUri(user.getUsername(), projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		String projectNameFromCreate = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("projectName").asText();
		assertEquals(projectName, projectNameFromCreate);
		String tagNameFromCreate = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("tagName").asText();
		assertEquals(projectTagCreateDto.getTagName(), tagNameFromCreate);
		
		mockMvc.perform(get(UriUtil.getProjectTagUri(user.getUsername(), projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk());
		
		mockMvc.perform(delete(UriUtil.getProjectTagUri(user.getUsername(), projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		mockMvc.perform(get(UriUtil.getProjectTagUri(user.getUsername(), projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@BeforeEach
	public void setup() throws FirebaseAuthException {
		authService.register(new SignupDto(email, username, password));
		user = userRepository.findByEmailIgnoreCase(email).get();
		user.setIsVerified(true);
		token = authService.login(new LoginDto(email, password)).getToken();
		project = projectRepository.save(new UserProject(user, "test project"));
		tag1 = tagRepository.save(new Tag("test tag 1"));
		tag2 = tagRepository.save(new Tag("test tag 2"));
	}
}
