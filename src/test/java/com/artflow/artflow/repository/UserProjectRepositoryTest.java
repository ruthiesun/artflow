package com.artflow.artflow.repository;

import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.UserProjectId;
import com.artflow.artflow.model.Visibility;
import jakarta.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserProjectRepositoryTest {
	private static final Logger LOG = LoggerFactory.getLogger(UserProjectRepositoryTest.class);
	private final String EMAIL = "testEmail";
	private final String PASSWORD = "testPassword";
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	
	@Test
	public void canCreateProject() {
		String projectName = "test project";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		assertFalse(projectRepository.findById(projectId).isPresent());
		assertEquals(0,projectRepository.count());
		
		projectRepository.save(project);
		
		assertEquals(1,projectRepository.count());
		Optional<UserProject> foundProject = projectRepository.findById(projectId);
		assertTrue(foundProject.isPresent());
		assertEquals(user.getId(), foundProject.get().getUser().getId());
		assertEquals(project.getId(), foundProject.get().getId());
		assertNotNull(foundProject.get().getCreatedDateTime());
		assertNotNull(foundProject.get().getUpdatedDateTime());
		assertEquals(
				foundProject.get().getCreatedDateTime().truncatedTo(ChronoUnit.SECONDS),
				foundProject.get().getUpdatedDateTime().truncatedTo(ChronoUnit.SECONDS)
		);
		assertNotNull(foundProject.get().getVisibility());
		assertEquals(Visibility.PRIVATE, foundProject.get().getVisibility());
	}
	
	@Test
	public void canCreateProjects() {
		String projectName1 = "test project 1";
		String projectName2 = "test project 2";
		
		UserProjectId projectId1 = new UserProjectId(user.getId(), projectName1);
		UserProject project1 = new UserProject(projectId1);
		project1.setUser(user);
		UserProjectId projectId2 = new UserProjectId(user.getId(), projectName2);
		UserProject project2 = new UserProject(projectId2);
		project2.setUser(user);
		assertFalse(projectRepository.findById(projectId1).isPresent());
		assertFalse(projectRepository.findById(projectId2).isPresent());
		assertEquals(0,projectRepository.count());
		
		projectRepository.save(project1);
		projectRepository.save(project2);
		
		assertEquals(2,projectRepository.count());
		Optional<UserProject> foundProject1 = projectRepository.findById(projectId1);
		assertTrue(foundProject1.isPresent());
		Optional<UserProject> foundProject2 = projectRepository.findById(projectId2);
		assertTrue(foundProject2.isPresent());
	}
	
	@Test
	public void cannotCreateProjectsWithSameName() {
		String projectName = "test project";
		
		UserProjectId projectId1 = new UserProjectId(user.getId(), projectName);
		UserProject project1 = new UserProject(projectId1);
		project1.setUser(user);
		UserProjectId projectId2 = new UserProjectId(user.getId(), projectName);
		UserProject project2 = new UserProject(projectId2);
		project2.setUser(user);
		
		projectRepository.save(project1);
		
		assertThrows(RuntimeException.class, () -> {
			projectRepository.saveAndFlush(project2);
		});
	}
	
	@Test
	public void cannotUpdateUserId() {
		String projectName = "test project";
		User user2 = new User("user2", "user2");
		userRepository.save(user2);
		user2 = userRepository.getReferenceById(user2.getId());
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		assertEquals(projectId, foundProject.getId());
		
		UserProjectId projectId2 = new UserProjectId(user2.getId(), projectName);
		assertThrows(RuntimeException.class, () -> {
			foundProject.setId(projectId2);
			entityManager.flush();
		});
	}
	
	@Test
	public void cannotUpdateProjectName() {
		String projectName1 = "test project 1";
		String projectName2 = "test project 2";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName1);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		
		UserProjectId projectId2 = new UserProjectId(user.getId(), projectName2);
		assertThrows(RuntimeException.class, () -> {
			foundProject.setId(projectId2);
			entityManager.flush();
		});
	}
	
	@Test
	public void canAddDescription() {
		String projectName = "test project";
		String projectDescription = "test description";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		project.setDescription(projectDescription);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		assertEquals(projectDescription, foundProject.getDescription());
	}
	
	@Test
	public void canUpdateDescription() {
		String projectName = "test project";
		String projectDescription1 = "test description 1";
		String projectDescription2 = "test description 2";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		project.setDescription(projectDescription1);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		assertEquals(projectDescription1, foundProject.getDescription());
		
		foundProject.setDescription(projectDescription2);
		entityManager.flush();
		entityManager.clear();
		
		foundProject = projectRepository.getReferenceById(projectId);
		assertEquals(projectDescription2, foundProject.getDescription());
		assertTrue(foundProject.getUpdatedDateTime().isAfter(foundProject.getCreatedDateTime()));
	}
	
	@Test
	public void canUpdateVisibility() {
		String projectName = "test project";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		
		foundProject.setVisibility(Visibility.PUBLIC);
		entityManager.flush();
		entityManager.clear();
		
		foundProject = projectRepository.getReferenceById(projectId);
		assertEquals(Visibility.PUBLIC, foundProject.getVisibility());
		assertTrue(foundProject.getUpdatedDateTime().isAfter(foundProject.getCreatedDateTime()));
	}
	
	@Test
	public void cannotNullifyVisibility() {
		String projectName = "test project";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		
		UserProject foundProject = projectRepository.getReferenceById(projectId);
		
		assertThrows(RuntimeException.class, () -> {
			foundProject.setVisibility(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void canDeleteProject() {
		String projectName = "test project";
		
		UserProjectId projectId = new UserProjectId(user.getId(), projectName);
		UserProject project = new UserProject(projectId);
		project.setUser(user);
		projectRepository.save(project);
		assertTrue(projectRepository.findById(projectId).isPresent());
		
		projectRepository.delete(project);
		assertFalse(projectRepository.findById(projectId).isPresent());
	}
	
	@Test
	public void deletionPropagatesToImages() {
	
	}
	
	@Test
	public void deletionPropagatesToProjectTags() {
	
	}
	
	@BeforeEach
	public void setup() {
		user = new User(EMAIL, PASSWORD);
		userRepository.save(user);
		user = userRepository.getReferenceById(user.getId());
		LOG.info("created test user");
	}
}
