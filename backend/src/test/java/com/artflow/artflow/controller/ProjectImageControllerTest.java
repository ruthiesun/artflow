package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.controller.common.JsonUtil;
import com.artflow.artflow.dto.ProjectCreateDto;
import com.artflow.artflow.dto.ProjectImageCreateDto;
import com.artflow.artflow.dto.ProjectImageUpdateDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.ProjectImageRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
		"jwt.signing-secret=test-secret"
})
public class ProjectImageControllerTest {
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private ProjectImageRepository projectImageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthService authService;
	
	private User user;
	private UserProject project;
	private String token;
	
	@Test
	public void canCreateProjectImage() throws Exception {
		ProjectImageCreateDto projectImageCreateDto = new ProjectImageCreateDto("a caption", LocalDateTime.now(), "url");
		
		MvcResult res = mockMvc.perform(post(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		int position = Integer.parseInt(objectMapper.readTree(res.getResponse().getContentAsString()).get("position").asText());
		assertEquals(0, position);
		Optional<ProjectImage> image = projectImageRepository.findByProject_ProjectNameAndProject_Owner_EmailAndPosition(
				project.getProjectName(), project.getOwner().getEmail(), position);
		assertTrue(image.isPresent());
		
		long numImages = projectImageRepository.countByProject_ProjectNameAndProject_Owner_Email(
				project.getProjectName(), project.getOwner().getEmail());
		assertEquals(1, numImages);
		
		assertEquals(1, project.getImages().size());
	}
	
	@Test
	public void canCreateProjectImages() throws Exception {
		ProjectImageCreateDto projectImageCreateDto1 = new ProjectImageCreateDto("a caption 2", LocalDateTime.now(), "url1");
		MvcResult res1 = mockMvc.perform(post(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageCreateDto1)))
				.andExpect(status().isCreated())
				.andReturn();
		
		ProjectImageCreateDto projectImageCreateDto2 = new ProjectImageCreateDto("a caption 1", LocalDateTime.now(), "url2");
		MvcResult res2 = mockMvc.perform(post(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageCreateDto2)))
				.andExpect(status().isCreated())
				.andReturn();
		
		int position1 = Integer.parseInt(objectMapper.readTree(res1.getResponse().getContentAsString()).get("position").asText());
		assertEquals(0, position1);
		int position2 = Integer.parseInt(objectMapper.readTree(res2.getResponse().getContentAsString()).get("position").asText());
		assertEquals(1, position2);
		
		long numImages = projectImageRepository.countByProject_ProjectNameAndProject_Owner_Email(
				project.getProjectName(), project.getOwner().getEmail());
		assertEquals(2, numImages);
		
		assertEquals(2, project.getImages().size());
	}
	
	@Test
	public void cannotCreateProjectImageForProjectThatDoesNotExist() throws Exception {
		ProjectImageCreateDto projectImageCreateDto = new ProjectImageCreateDto("a caption", LocalDateTime.now(), "url");
		
		mockMvc.perform(post(UriUtil.getImagesUri("yah"))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageCreateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void canGetProjectImageForProject() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		project.getImages().add(image1);
		project.getImages().add(image2);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getImageUri(project.getProjectName(), image1.getId()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<JsonUtil.Field> expected = new HashSet<>(List.of(
				new JsonUtil.Field("url", image1.getUrl()))
		);
		JsonUtil.checkMockResponse(objectMapper, expected, res);
	}
	
	@Test
	public void cannotGetProjectImageForProjectThatDoesNotExist() throws Exception {
		mockMvc.perform(get(UriUtil.getImageUri("yah", 0L))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void cannotGetProjectImageThatDoesNotExist() throws Exception {
		mockMvc.perform(get(UriUtil.getImageUri(project.getProjectName(), 0L))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void canGetProjectImagesForProject() throws Exception {
		UserProject otherProject = new UserProject(user, "other project");
		projectRepository.save(otherProject);
		
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		ProjectImage image3 = new ProjectImage(otherProject, 0, "url3");
		project.getImages().add(image1);
		project.getImages().add(image2);
		otherProject.getImages().add(image3);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		projectImageRepository.save(image3);
		
		MvcResult res = mockMvc.perform(get(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<Set<JsonUtil.Field>> expected = new HashSet<>(List.of(
				new HashSet<>(List.of(new JsonUtil.Field("url", image1.getUrl()))),
				new HashSet<>(List.of(new JsonUtil.Field("url", image2.getUrl())))
		));
		JsonUtil.checkMockResponses(objectMapper, expected, res);
	}
	
	@Test
	public void canUpdateProjectImage() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		project.getImages().add(image1);
		project.getImages().add(image2);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		
		ProjectImageUpdateDto projectImageUpdateDto = new ProjectImageUpdateDto(
				image1.getId(),
				image1.getPosition(),
				image1.getCaption(),
				image1.getDateTime(),
				"url3"
		);
		MvcResult res = mockMvc.perform(put(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageUpdateDto)))
				.andExpect(status().isOk())
				.andReturn();
		
		Set<JsonUtil.Field> expected = new HashSet<>(List.of(
				new JsonUtil.Field("url", projectImageUpdateDto.getUrl()))
		);
		JsonUtil.checkMockResponse(objectMapper, expected, res);
	}
	
	@Test
	public void canIncreaseProjectImagePosition() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		ProjectImage image3 = new ProjectImage(project, 2, "url3");
		project.getImages().add(image1);
		project.getImages().add(image2);
		project.getImages().add(image3);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		projectImageRepository.save(image3);
		
		ProjectImageUpdateDto projectImageUpdateDto = new ProjectImageUpdateDto(
				image1.getId(),
				2,
				image1.getCaption(),
				image1.getDateTime(),
				image1.getUrl()
		);
		mockMvc.perform(put(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageUpdateDto)))
				.andExpect(status().isOk());
		
		List<ProjectImage> images = projectImageRepository.findByProject_ProjectNameAndProject_Owner_EmailOrderByPosition(
				project.getProjectName(), project.getOwner().getEmail());
		assertEquals(0, images.get(0).getPosition());
		assertEquals(1, images.get(1).getPosition());
		assertEquals(2, images.get(2).getPosition());
		assertEquals(image2.getId(), images.get(0).getId());
		assertEquals(image3.getId(), images.get(1).getId());
		assertEquals(image1.getId(), images.get(2).getId());
	}
	
	@Test
	public void canDecreaseProjectImagePosition() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		ProjectImage image3 = new ProjectImage(project, 2, "url3");
		project.getImages().add(image1);
		project.getImages().add(image2);
		project.getImages().add(image3);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		projectImageRepository.save(image3);
		
		ProjectImageUpdateDto projectImageUpdateDto = new ProjectImageUpdateDto(
				image3.getId(),
				0,
				image3.getCaption(),
				image3.getDateTime(),
				image3.getUrl()
		);
		mockMvc.perform(put(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageUpdateDto)))
				.andExpect(status().isOk());
		
		List<ProjectImage> images = projectImageRepository.findByProject_ProjectNameAndProject_Owner_EmailOrderByPosition(
				project.getProjectName(), project.getOwner().getEmail());
		assertEquals(0, images.get(0).getPosition());
		assertEquals(1, images.get(1).getPosition());
		assertEquals(2, images.get(2).getPosition());
		assertEquals(image3.getId(), images.get(0).getId());
		assertEquals(image1.getId(), images.get(1).getId());
		assertEquals(image2.getId(), images.get(2).getId());
	}
	
	@Test
	public void cannotUpdateProjectImageThatDoesNotExist() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		project.getImages().add(image1);
		projectImageRepository.save(image1);
		
		ProjectImageUpdateDto projectImageUpdateDto = new ProjectImageUpdateDto(
				123L,
				image1.getPosition(),
				image1.getCaption(),
				image1.getDateTime(),
				image1.getUrl()
		);
		
		mockMvc.perform(put(UriUtil.getImagesUri(project.getProjectName()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageUpdateDto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void canDeleteProjectImage() throws Exception {
		ProjectImage image1 = new ProjectImage(project, 0, "url1");
		ProjectImage image2 = new ProjectImage(project, 1, "url2");
		ProjectImage image3 = new ProjectImage(project, 2, "url3");
		project.getImages().add(image1);
		project.getImages().add(image2);
		project.getImages().add(image3);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		projectImageRepository.save(image3);
		
		mockMvc.perform(delete(UriUtil.getImageUri(project.getProjectName(), image2.getId()))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		List<ProjectImage> images = projectImageRepository.findByProject_ProjectNameAndProject_Owner_EmailOrderByPosition(
				project.getProjectName(), project.getOwner().getEmail());
		assertEquals(0, images.get(0).getPosition());
		assertEquals(image1.getUrl(), images.get(0).getUrl());
		assertEquals(1, images.get(1).getPosition());
		assertEquals(image3.getUrl(), images.get(1).getUrl());
		assertEquals(2, images.size());
	}
	
	@Test
	public void canDeleteProjectImageThatDoesNotExist() throws Exception {
		mockMvc.perform(delete(UriUtil.getImageUri(project.getProjectName(), 123L))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void canCreateProjectAndManageImages() throws Exception {
		ProjectCreateDto projectCreateDto = new ProjectCreateDto("a project", "desc", Visibility.PUBLIC);
		MvcResult projectCreateResult = mockMvc.perform(post(UriUtil.getProjectsUri())
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		String projectName = objectMapper.readTree(projectCreateResult.getResponse().getContentAsString()).get("projectName").asText();
		
		ProjectImageCreateDto projectImageCreateDto = new ProjectImageCreateDto("a caption", LocalDateTime.now(), "url");
		MvcResult createResult = mockMvc.perform(post(UriUtil.getImagesUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageCreateDto)))
				.andExpect(status().isCreated())
				.andReturn();
		
		Long imageId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();
		Integer position = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("position").asInt();
		String caption = "a new caption";
		LocalDateTime dateTime = LocalDateTime.parse(objectMapper.readTree(createResult.getResponse().getContentAsString()).get("dateTime").asText());
		String url = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("url").asText();
		String projectNameFromCreate = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("projectName").asText();
		assertEquals(projectName, projectNameFromCreate);
		
		ProjectImageUpdateDto projectImageUpdateDto = new ProjectImageUpdateDto(
				imageId,
				position,
				caption,
				dateTime,
				url
		);
		MvcResult updateResult = mockMvc.perform(put(UriUtil.getImagesUri(projectName))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(projectImageUpdateDto)))
				.andExpect(status().isOk())
				.andReturn();
		
		imageId = objectMapper.readTree(updateResult.getResponse().getContentAsString()).get("id").asLong();
		String projectNameFromUpdate = objectMapper.readTree(updateResult.getResponse().getContentAsString()).get("projectName").asText();
		String captionFromUpdate = objectMapper.readTree(updateResult.getResponse().getContentAsString()).get("caption").asText();
		assertEquals(projectName, projectNameFromUpdate);
		assertEquals(caption, captionFromUpdate);
		
		mockMvc.perform(get(UriUtil.getImageUri(projectName, imageId))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isOk());
		
		mockMvc.perform(delete(UriUtil.getImageUri(projectName, imageId))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
		
		mockMvc.perform(get(UriUtil.getImageUri(projectName, imageId))
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNotFound());
	}
	
	@BeforeEach
	public void setup() {
		user = new User("testemail", "testpassword");
		token = authService.register(new SignupDto(user.getEmail(), user.getPassword())).getToken();
		user = userRepository.findByEmail(user.getEmail()).get();
		project = projectRepository.save(new UserProject(user, "test project"));
	}
}
