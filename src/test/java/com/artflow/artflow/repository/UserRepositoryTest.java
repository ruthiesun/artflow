package com.artflow.artflow.repository;

import com.artflow.artflow.model.User;
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
public class UserRepositoryTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryTest.class);
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void canCreateUser() {
		String email = "testEmail";
		String password = "testPassword";
		
		assertEquals(0, userRepository.count());
		
		User user = new User(email, password);
		userRepository.save(user);
		
		assertEquals(1, userRepository.count());
		Optional<User> foundUser = userRepository.findById(user.getId());
		assertTrue(foundUser.isPresent());
		assertEquals(user.getId(), foundUser.get().getId());
		assertEquals(user.getEmail(), foundUser.get().getEmail());
		assertEquals(user.getPassword(), foundUser.get().getPassword());
	}
	
	@Test
	public void canCreateUsers() {
		String email1 = "testEmail1";
		String password1 = "testPassword1";
		String email2 = "testEmail2";
		String password2 = "testPassword2";
		
		assertEquals(0, userRepository.count());
		
		User user1 = new User(email1, password1);
		User user2 = new User(email2, password2);
		userRepository.save(user1);
		userRepository.save(user2);
		
		assertEquals(2, userRepository.count());
		assertTrue(userRepository.findById(user1.getId()).isPresent());
		assertTrue(userRepository.findById(user2.getId()).isPresent());
	}
	
	@Test
	public void cannotCreateUsersWithSameEmail() {
		String email = "testEmail";
		String password1 = "testPassword1";
		String password2 = "testPassword2";
		
		User user1 = new User(email, password1);
		User user2 = new User(email, password2);
		userRepository.save(user1);
		
		assertThrows(RuntimeException.class, () -> {
				userRepository.saveAndFlush(user2);
		});
	}
	
	@Test
	public void canUpdateEmail() {
		String email1 = "testEmail1";
		String email2 = "testEmail2";
		String password = "testPassword";
		
		User user = new User(email1, password);
		userRepository.save(user);
		User foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(email1, foundUser.getEmail());
		
		foundUser.setEmail(email2);
		entityManager.flush();
		entityManager.clear();
		
		foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(email2, foundUser.getEmail());
	}
	
	@Test
	public void cannotNullifyEmail() {
		String email = "testEmail";
		String password = "testPassword";
		
		User user = new User(email, password);
		userRepository.save(user);
		User foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(email, foundUser.getEmail());
		
		assertThrows(RuntimeException.class, () -> {
			foundUser.setEmail(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void cannotUpdateUsersWithSameEmail() {
		String email1 = "testEmail1";
		String password1 = "testPassword1";
		String email2 = "testEmail2";
		String password2 = "testPassword2";
		
		User user1 = new User(email1, password1);
		User user2 = new User(email2, password2);
		userRepository.save(user1);
		userRepository.save(user2);
		
		assertThrows(RuntimeException.class, () -> {
			userRepository.getReferenceById(user2.getId()).setEmail(email1);
			entityManager.flush();
		});
	}
	
	@Test
	public void canUpdatePassword() {
		String email = "testEmail";
		String password1 = "testPassword1";
		String password2 = "testPassword2";
		
		User user = new User(email, password1);
		userRepository.save(user);
		User foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(password1, foundUser.getPassword());
		
		foundUser.setPassword(password2);
		entityManager.flush();
		entityManager.clear();
		
		foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(password2, foundUser.getPassword());
	}
	
	@Test
	public void cannotNullifyPassword() {
		String email = "testEmail";
		String password = "testPassword";
		
		User user = new User(email, password);
		userRepository.save(user);
		User foundUser = userRepository.getReferenceById(user.getId());
		assertEquals(password, foundUser.getPassword());
		
		assertThrows(RuntimeException.class, () -> {
			foundUser.setPassword(null);
			entityManager.flush();
		});
	}
	
	@Test
	public void canDeleteUser() {
		String email = "testEmail";
		String password = "testPassword";
		
		User user = new User(email, password);
		userRepository.save(user);
		assertEquals(1, userRepository.count());
		
		userRepository.delete(userRepository.getReferenceById(user.getId()));
		assertEquals(0, userRepository.count());
	}
	
	@Test
	public void deletionPropagatesToProjects() {
	
	}
}
