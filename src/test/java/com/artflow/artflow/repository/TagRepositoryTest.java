package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Tag;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.UserProjectId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TagRepositoryTest {
	private static final Logger LOG = LoggerFactory.getLogger(TagRepositoryTest.class);
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserProjectRepository projectRepository;
	
	@Autowired
	private ProjectTagRepository projectTagRepository;
	
	@Test
	public void canCreateTag() {
		String name = "test tag";
		
		assertEquals(0, tagRepository.count());
		
		Tag tag = new Tag(name);
		tagRepository.save(tag);

		assertEquals(1, tagRepository.count());
		Optional<Tag> foundTag = tagRepository.findById(tag.getId());
		assertTrue(foundTag.isPresent());
		assertEquals(tag.getId(), foundTag.get().getId());
		assertEquals(tag.getName(), foundTag.get().getName());
	}
	
	@Test
	public void canCreateTags() {
		String name1 = "test tag 1";
		String name2 = "test tag 2";
		
		assertEquals(0, tagRepository.count());
		
		Tag tag1 = new Tag(name1);
		Tag tag2 = new Tag(name2);
		tagRepository.save(tag1);
		tagRepository.save(tag2);
		
		assertEquals(2, tagRepository.count());
		assertTrue(tagRepository.findById(tag1.getId()).isPresent());
		assertTrue(tagRepository.findById(tag2.getId()).isPresent());
	}
	
	@Test
	public void cannotCreateTagsWithSameName() {
		String name = "test tag";

		Tag tag1 = new Tag(name);
		Tag tag2 = new Tag(name);
		tagRepository.save(tag1);
		
		assertThrows(RuntimeException.class, () -> {
			tagRepository.saveAndFlush(tag2);
		});
	}
	
	@Test
	public void canUpdateName() {
		String name1 = "test tag 1";
		String name2 = "test tag 2";
		
		Tag tag = new Tag(name1);
		tagRepository.save(tag);
		Tag foundTag = tagRepository.getReferenceById(tag.getId());
		foundTag.setName(name2);
		entityManager.flush();
		entityManager.clear();
		
		foundTag = tagRepository.getReferenceById(tag.getId());
		assertEquals(name2, foundTag.getName());
	}
	
	@Test
	public void cannotNullifyName() {
		String name = "test tag";
		
		Tag tag = new Tag(name);
		tagRepository.save(tag);
		
		assertThrows(RuntimeException.class, () -> {
			tagRepository.getReferenceById(tag.getId()).setName(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void cannotUpdateTagsWithSameName() {
		String name1 = "test tag 1";
		String name2 = "test tag 2";
		
		Tag tag1 = new Tag(name1);
		Tag tag2 = new Tag(name2);
		tagRepository.save(tag1);
		tagRepository.save(tag2);
		
		assertThrows(RuntimeException.class, () -> {
			tagRepository.getReferenceById(tag2.getId()).setName(name1);
			entityManager.flush();
		});
	}

	@Test
	public void canDeleteTag() {
		String name = "test tag";
		
		Tag tag = new Tag(name);
		tagRepository.save(tag);
		assertEquals(1, tagRepository.count());
		tagRepository.delete(tag);
		
		assertEquals(0, tagRepository.count());
	}
	
	@Test
	public void deletionPropagatesToProjectTags() {
		String projectName1 = "test project 1";
		String projectName2 = "test project 2";
		String tagName1 = "test tag 1";
		String tagName2 = "test tag 2";

		User user = new User("email", "password");
		userRepository.save(user);
		user = userRepository.getReferenceById(user.getId());

		// create projects
		UserProject project1 = new UserProject(user, projectName1);
		UserProject project2 = new UserProject(user, projectName2);
		projectRepository.save(project1);
		projectRepository.save(project2);
		assertEquals(2, projectRepository.count());

		// create tags
		Tag tag1 = new Tag(tagName1);
		Tag tag2 = new Tag(tagName2);
		tagRepository.save(tag1);
		tagRepository.save(tag2);
		assertEquals(2, tagRepository.count());

		// add tags to first project
		ProjectTag projectTag1 = new ProjectTag(new ProjectTagId(project1.getId(), tag1.getId()));
		projectTag1.setProject(project1);
		projectTag1.setTag(tag1);
		ProjectTag projectTag2 = new ProjectTag(new ProjectTagId(project1.getId(), tag2.getId()));
		projectTag2.setProject(project1);
		projectTag2.setTag(tag2);
		projectTagRepository.save(projectTag1);
		projectTagRepository.save(projectTag2);
		assertEquals(2, projectTagRepository.count());

		// add tags to second project
		ProjectTag projectTag1Project2 = new ProjectTag(new ProjectTagId(project2.getId(), tag1.getId()));
		projectTag1Project2.setProject(project2);
		projectTag1Project2.setTag(tag1);
		projectTagRepository.save(projectTag1Project2);
		assertEquals(3, projectTagRepository.count());

		// test that project tags are available in tag class
		entityManager.flush();
		entityManager.clear();
		tag1 = tagRepository.getReferenceById(tag1.getId());
		assertEquals(2, tag1.getProjectTags().size());

		// test that project tags are deleted if tags are deleted
		tagRepository.delete(tag1);
		assertEquals(2, projectRepository.count());
		assertEquals(1, projectTagRepository.count());
		assertEquals(1, tagRepository.count());
	}
}
