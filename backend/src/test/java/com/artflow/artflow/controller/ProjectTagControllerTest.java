package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.controller.common.JsonUtil;
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
		"jwt.signing-secret=test-secret"
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
	
	private User user;
	private UserProject project;
	private Tag tag1;
	private Tag tag2;
	private String token;
	
	@Test
	public void canCreateProjectTagExistingTag() throws Exception {
		String projectName = project.getProjectName();
		String tagName = tag1.getName();
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated());
		
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findByTagNameAndProject_ProjectNameAndProject_Owner_Email(
				tagName, projectName, project.getOwner().getEmail());
		assertTrue(foundProjectTag.isPresent());
	}
	
	@Test
	public void canCreateProjectTagNotExistingTag() throws Exception {
		String projectName = project.getProjectName();
		String tagName = "some tag";
		
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, tagName);
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated());
		
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findByTagNameAndProject_ProjectNameAndProject_Owner_Email(
				tagName, projectName, project.getOwner().getEmail());
		assertTrue(foundProjectTag.isPresent());
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
		
		mockMvc.perform(post(UriUtil.getProjectTagsUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isConflict());
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
		
		MvcResult res = mockMvc.perform(get(UriUtil.getTagsUri())
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
		MvcResult res = mockMvc.perform(get(UriUtil.getTagsUri())
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
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagUri(project.getProjectName(), tag1.getName()))
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
		
		MvcResult res = mockMvc.perform(get(UriUtil.getProjectTagsUri(project.getProjectName()))
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
	public void canDeleteProjectTag() throws Exception {
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);
		
		mockMvc.perform(delete(UriUtil.getProjectTagUri(project.getProjectName(), tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		assertEquals(0L, projectTagRepository.count());
	}
	
	@Test
	public void canDeleteProjectTagThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getProjectTagUri(project.getProjectName(), tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void canDeleteProjectTagForProjectThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getProjectTagUri("asdf", tag1.getName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void canUpdateProjectTimestampWithTag() throws Exception {
		// create project
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri())
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectCreateDto)))
			.andExpect(status().isCreated())
			.andReturn();
		
		String projectName = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("projectName").asText();
		LocalDateTime updatedDateTime1 = LocalDateTime.parse(objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		
		// create tag
		ProjectTagCreateDto projectTagCreateDto = new ProjectTagCreateDto(projectName, "an excellent tag");
		MvcResult tagCreateResult = mockMvc.perform(post(UriUtil.getProjectTagsUri(projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
			.andExpect(status().isCreated())
			.andReturn();
		
		String tagName = objectMapper.readTree(tagCreateResult.getResponse().getContentAsString()).get("tagName").asText();
		
		// check update timestamp is different
		MvcResult projectGetResult = mockMvc.perform(get(UriUtil.getProjectUri(projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		LocalDateTime updatedDateTime2 = LocalDateTime.parse(objectMapper.readTree(projectGetResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		assertTrue(updatedDateTime2.isAfter(updatedDateTime1));
		
		// delete tag
		mockMvc.perform(delete(UriUtil.getProjectTagUri(projectName, tagName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isNoContent());
		
		// check update timestamp is different
		projectGetResult = mockMvc.perform(get(UriUtil.getProjectUri(projectName))
				.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
			.andExpect(status().isOk())
			.andReturn();
		
		LocalDateTime updatedDateTime3 = LocalDateTime.parse(objectMapper.readTree(projectGetResult.getResponse().getContentAsString()).get("updatedDateTime").asText());
		assertTrue(updatedDateTime3.isAfter(updatedDateTime2));
	}
	
	@Test
	public void canCreateProjectAndManageTags() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri())
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
		MvcResult createResult = mockMvc.perform(post(UriUtil.getProjectTagsUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectTagCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		String projectNameFromCreate = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("projectName").asText();
		assertEquals(projectName, projectNameFromCreate);
		String tagNameFromCreate = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("tagName").asText();
		assertEquals(projectTagCreateDto.getTagName(), tagNameFromCreate);
		
		mockMvc.perform(get(UriUtil.getProjectTagUri(projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk());
		
		mockMvc.perform(delete(UriUtil.getProjectTagUri(projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		mockMvc.perform(get(UriUtil.getProjectTagUri(projectName, tagNameFromCreate))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@BeforeEach
	public void setup() {
		user = new User("testemail", "testpassword");
		token = authService.register(new SignupDto(user.getEmail(), user.getPassword())).getToken();
		user = userRepository.findByEmail(user.getEmail()).get();
		project = projectRepository.save(new UserProject(user, "test project"));
		tag1 = tagRepository.save(new Tag("test tag 1"));
		tag2 = tagRepository.save(new Tag("test tag 2"));
	}
}
