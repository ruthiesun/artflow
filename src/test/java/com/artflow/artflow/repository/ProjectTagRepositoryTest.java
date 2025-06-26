package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ProjectTagRepositoryTest {
	private static final Logger log = LoggerFactory.getLogger(ProjectTagRepositoryTest.class);
	private final String email = "testEmail";
	private final String password = "testPassword";
	private final String projectName = "test project";
	private final String tagName1 = "tag 1";
	private final String tagName2 = "tag 2";
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private ProjectTagRepository projectTagRepository;
	
	private User user;
	private UserProject project;
	private Tag tag1;
	private Tag tag2;
	
	
	@Test
	public void canCreateProjectTag() {
		assertEquals(0, projectTagRepository.count());
		
		ProjectTag projectTag = new ProjectTag(new ProjectTagId(project.getId(), tag1.getId()));
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);
		
		assertEquals(1, projectTagRepository.count());
		Optional<ProjectTag> foundProjectTag = projectTagRepository.findById(projectTag.getId());
		assertTrue(foundProjectTag.isPresent());
		assertEquals(project, foundProjectTag.get().getProject());
	}
	
	@Test
	public void canCreateProjectTags() {
		assertEquals(0, projectTagRepository.count());

		ProjectTagId projectTagId1 = new ProjectTagId(project.getId(), tag1.getId());
		ProjectTag projectTag1 = new ProjectTag(projectTagId1);
		projectTag1.setProject(project);
		projectTag1.setTag(tag1);
		ProjectTagId projectTagId2 = new ProjectTagId(project.getId(), tag2.getId());
		ProjectTag projectTag2 = new ProjectTag(projectTagId2);
		projectTag2.setProject(project);
		projectTag2.setTag(tag2);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);

		assertEquals(2, projectTagRepository.count());
		Optional<ProjectTag> foundProjectTag1 = projectTagRepository.findById(projectTagId1);
		assertTrue(foundProjectTag1.isPresent());
		Optional<ProjectTag> foundProjectTag2 = projectTagRepository.findById(projectTagId2);
		assertTrue(foundProjectTag2.isPresent());
	}
	
	@Test
	public void cannotCreateProjectTagsWithSameTagId() {
		assertEquals(0, projectTagRepository.count());

		ProjectTagId projectTagId1 = new ProjectTagId(project.getId(), tag1.getId());
		ProjectTag projectTag1 = new ProjectTag(projectTagId1);
		projectTag1.setProject(project);
		projectTag1.setTag(tag1);
		ProjectTagId projectTagId2 = new ProjectTagId(project.getId(), tag1.getId());
		ProjectTag projectTag2 = new ProjectTag(projectTagId2);
		projectTag2.setProject(project);
		projectTag2.setTag(tag1);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		
		assertEquals(1, projectTagRepository.count());
	}
	
	@Test
	public void canDeleteProjectTag() {
		ProjectTagId projectTagId = new ProjectTagId(project.getId(), tag1.getId());
		ProjectTag projectTag = new ProjectTag(projectTagId);
		projectTag.setProject(project);
		projectTag.setTag(tag1);
		projectTagRepository.save(projectTag);

		assertEquals(1, projectTagRepository.count());
		ProjectTag foundProjectTag = projectTagRepository.getReferenceById(projectTagId);
		projectTagRepository.delete(foundProjectTag);
		
		assertEquals(0, projectTagRepository.count());
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
		
		tag1 = new Tag(tagName1);
		tag2 = new Tag(tagName2);
		tagRepository.save(tag1);
		tagRepository.save(tag2);
		tag1 = tagRepository.getReferenceById(tag1.getId());
		tag2 = tagRepository.getReferenceById(tag2.getId());
		log.info("created test tags");
	}
}
