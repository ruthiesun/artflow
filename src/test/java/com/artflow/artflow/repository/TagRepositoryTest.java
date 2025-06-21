package com.artflow.artflow.repository;

import com.artflow.artflow.model.Tag;
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
	
	}
}
