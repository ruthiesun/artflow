package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.ProjectImageId;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.UserProjectId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ProjectImageRepositoryTest {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectImageRepositoryTest.class);
	private final String EMAIL = "testEmail";
	private final String PASSWORD = "testPassword";
	private final String PROJECT_NAME = "test project";
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private ProjectImageRepository projectImageRepository;
	
	private User user;
	private UserProject project;
	
	@Test
	public void canCreateImage() {
		String url = "testUrl";
		int position = 0;
		
		assertEquals(0, projectImageRepository.count());
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url);
		image.setProject(project);
		projectImageRepository.save(image);
		
		assertEquals(1, projectImageRepository.count());
		Optional<ProjectImage> foundProjectImage = projectImageRepository.findById(imageId);
		assertTrue(foundProjectImage.isPresent());
	}
	
	@Test
	public void canCreateImages() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position1 = 0;
		int position2 = 1;
		
		assertEquals(0, projectImageRepository.count());
		
		ProjectImageId imageId1 = new ProjectImageId(project.getId(), position1);
		ProjectImage image1 = new ProjectImage(imageId1, url1);
		image1.setProject(project);
		ProjectImageId imageId2 = new ProjectImageId(project.getId(), position2);
		ProjectImage image2 = new ProjectImage(imageId2, url2);
		image2.setProject(project);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		
		assertEquals(2, projectImageRepository.count());
		assertTrue(projectImageRepository.findById(imageId1).isPresent());
		assertTrue(projectImageRepository.findById(imageId2).isPresent());
	}
	
	@Test
	public void canUpdateCaption() {
		String url = "testUrl";
		int position = 0;
		String caption = "test caption";
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url);
		image.setProject(project);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(imageId);
		assertNull(foundImage.getCaption());
		
		foundImage.setCaption(caption);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(caption, projectImageRepository.getReferenceById(imageId).getCaption());
	}
	
	@Test
	public void canUpdateDateTime() {
		String url = "testUrl";
		int position = 0;
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url);
		image.setProject(project);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(imageId);
		assertNull(foundImage.getCaption());
		
		foundImage.setDateTime(currentDateTime);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(
				currentDateTime.truncatedTo(ChronoUnit.SECONDS),
				projectImageRepository.getReferenceById(imageId).getDateTime().truncatedTo(ChronoUnit.SECONDS)
		);
	}
	
	@Test
	public void canUpdateUrl() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position = 0;
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url1);
		image.setProject(project);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(imageId);
		assertEquals(url1, foundImage.getUrl());
		
		foundImage.setUrl(url2);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(url2, projectImageRepository.getReferenceById(imageId).getUrl());
	}
	
	@Test
	public void cannotNullifyUrl() {
		String url = "testUrl";
		int position = 0;
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url);
		image.setProject(project);
		projectImageRepository.save(image);
		
		assertThrows(RuntimeException.class, () -> {
			projectImageRepository.getReferenceById(imageId).setUrl(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void canDeleteImage() {
		String url = "testUrl";
		int position = 0;
		
		ProjectImageId imageId = new ProjectImageId(project.getId(), position);
		ProjectImage image = new ProjectImage(imageId, url);
		image.setProject(project);
		projectImageRepository.save(image);
		
		assertEquals(1, projectImageRepository.count());
		projectImageRepository.delete(projectImageRepository.getReferenceById(imageId));
		assertEquals(0, projectImageRepository.count());
	}
	
	@BeforeEach
	public void setup() {
		user = new User(EMAIL, PASSWORD);
		userRepository.save(user);
		user = userRepository.getReferenceById(user.getId());
		LOG.info("created test user");
		
		UserProjectId projectId = new UserProjectId(user.getId(), PROJECT_NAME);
		project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		project = projectRepository.getReferenceById(projectId);
		LOG.info("created test project");
	}
}
