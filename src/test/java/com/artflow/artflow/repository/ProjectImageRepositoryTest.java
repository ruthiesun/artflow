package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
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
	private static final Logger log = LoggerFactory.getLogger(ProjectImageRepositoryTest.class);
	private final String email = "testEmail";
	private final String password = "testPassword";
	private final String projectName = "test project";
	
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
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		
		assertEquals(1, projectImageRepository.count());
		Optional<ProjectImage> foundProjectImage = projectImageRepository.findById(image.getId());
		assertTrue(foundProjectImage.isPresent());
	}
	
	@Test
	public void canCreateImages() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position1 = 0;
		int position2 = 1;
		
		assertEquals(0, projectImageRepository.count());
		
		ProjectImage image1 = new ProjectImage(project, position1, url1);
		ProjectImage image2 = new ProjectImage(project, position2, url2);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		
		assertEquals(2, projectImageRepository.count());
		assertTrue(projectImageRepository.findById(image1.getId()).isPresent());
		assertTrue(projectImageRepository.findById(image2.getId()).isPresent());
	}
	
	@Test
	public void cannotCreateImagesWithSamePosition() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position = 0;
		
		ProjectImage image1 = new ProjectImage(project, position, url1);
		ProjectImage image2 = new ProjectImage(project, position, url2);
		projectImageRepository.save(image1);
		
		assertThrows(RuntimeException.class, () -> {
			projectImageRepository.saveAndFlush(image2);
		});
	}
	
	@Test
	public void canUpdatePosition() {
		String url = "testUrl";
		int position1 = 0;
		int position2 = 1;
		
		ProjectImage image = new ProjectImage(project, position1, url);
		projectImageRepository.save(image);
		image.setPosition(position2);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(position2, projectImageRepository.getReferenceById(image.getId()).getPosition());
	}
	
	@Test
	public void cannotUpdateImagesWithSamePosition() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position1 = 0;
		int position2 = 1;
		
		ProjectImage image1 = new ProjectImage(project, position1, url1);
		ProjectImage image2 = new ProjectImage(project, position2, url2);
		projectImageRepository.save(image1);
		projectImageRepository.save(image2);
		
		assertThrows(RuntimeException.class, () -> {
			image1.setPosition(position2);
			entityManager.flush();
			entityManager.clear();
		});
	}
	
	@Test
	public void cannotNullifyPosition() {
		String url = "testUrl";
		int position = 0;
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		
		assertThrows(RuntimeException.class, () -> {
			image.setPosition(null);
			entityManager.flush();
			entityManager.clear();
		});
	}
	
	@Test
	public void canUpdateCaption() {
		String url = "testUrl";
		int position = 0;
		String caption = "test caption";
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(image.getId());
		assertNull(foundImage.getCaption());
		
		foundImage.setCaption(caption);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(caption, projectImageRepository.getReferenceById(image.getId()).getCaption());
	}
	
	@Test
	public void canUpdateDateTime() {
		String url = "testUrl";
		int position = 0;
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(image.getId());
		assertNull(foundImage.getCaption());
		
		foundImage.setDateTime(currentDateTime);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(
				currentDateTime.truncatedTo(ChronoUnit.SECONDS),
				projectImageRepository.getReferenceById(image.getId()).getDateTime().truncatedTo(ChronoUnit.SECONDS)
		);
	}
	
	@Test
	public void canUpdateUrl() {
		String url1 = "testUrl1";
		String url2 = "testUrl2";
		int position = 0;
		
		ProjectImage image = new ProjectImage(project, position, url1);
		projectImageRepository.save(image);
		ProjectImage foundImage = projectImageRepository.getReferenceById(image.getId());
		assertEquals(url1, foundImage.getUrl());
		
		foundImage.setUrl(url2);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(url2, projectImageRepository.getReferenceById(image.getId()).getUrl());
	}
	
	@Test
	public void cannotNullifyUrl() {
		String url = "testUrl";
		int position = 0;
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		
		assertThrows(RuntimeException.class, () -> {
			projectImageRepository.getReferenceById(image.getId()).setUrl(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void canDeleteImage() {
		String url = "testUrl";
		int position = 0;
		
		ProjectImage image = new ProjectImage(project, position, url);
		projectImageRepository.save(image);
		
		assertEquals(1, projectImageRepository.count());
		projectImageRepository.delete(projectImageRepository.getReferenceById(image.getId()));
		assertEquals(0, projectImageRepository.count());
	}
	
	@BeforeEach
	public void setup() {
		user = new User(email, password);
		userRepository.save(user);
		user = userRepository.getReferenceById(user.getId());
		log.info("created test user");
		
		project = new UserProject(user, projectName);
		projectRepository.save(project);
		project = projectRepository.getReferenceById(project.getId());
		log.info("created test project");
	}
}
