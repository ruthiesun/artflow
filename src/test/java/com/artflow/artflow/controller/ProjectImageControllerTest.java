package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.controller.common.JsonUtil;
import com.artflow.artflow.dto.ProjectImageCreateDto;
import com.artflow.artflow.dto.ProjectImageUpdateDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.repository.ProjectImageRepository;
import com.artflow.artflow.repository.ProjectTagRepository;
import com.artflow.artflow.repository.TagRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserProjectRepositoryTest;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.service.AuthService;
import com.artflow.artflow.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
		
		MvcResult res = mockMvc.perform(post("/api/projects/images/" + project.getProjectName())
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
	public void cannotCreateProjectImageForProjectThatDoesNotExist() throws Exception {
		ProjectImageCreateDto projectImageCreateDto = new ProjectImageCreateDto("a caption", LocalDateTime.now(), "url");
		
		mockMvc.perform(post("/api/projects/images/yuh")
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
		
		MvcResult res = mockMvc.perform(get("/api/projects/images/" + project.getProjectName() + "/" + image1.getId())
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
		mockMvc.perform(get("/api/projects/images/yuhh")
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
		
		MvcResult res = mockMvc.perform(get("/api/projects/images/" + project.getProjectName())
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
		MvcResult res = mockMvc.perform(put("/api/projects/images")
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
	public void canUpdateProjectImagePosition() throws Exception {
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
				1,
				image1.getCaption(),
				image1.getDateTime(),
				image1.getUrl()
		);
		mockMvc.perform(put("/api/projects/images")
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
		assertEquals(image1.getId(), images.get(1).getId());
		assertEquals(image3.getId(), images.get(2).getId());
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
		
		mockMvc.perform(put("/api/projects/images")
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
		
		mockMvc.perform(delete("/api/projects/images/" + project.getProjectName() + "/" + image2.getId())
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
		mockMvc.perform(delete("/api/projects/images/" + project.getProjectName() + "/123")
						.header(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_TOKEN_PREAMBLE + token))
				.andExpect(status().isNoContent());
	}
	
	@BeforeEach
	public void setup() {
		user = new User("testemail", "testpassword");
		token = authService.register(new SignupDto(user.getEmail(), user.getPassword())).getToken();
		user = userRepository.findByEmail(user.getEmail()).get();
		project = projectRepository.save(new UserProject(user, "test project"));
	}
}
