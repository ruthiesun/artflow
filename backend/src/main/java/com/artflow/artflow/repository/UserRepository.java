package com.artflow.artflow.repository;

import com.artflow.artflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailIgnoreCase(String email);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE LOWER(u.email) = LOWER(:email)")
	Optional<User> findByEmailIgnoreCaseWithProjects(@Param("email") String email);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.id = :id")
	Optional<User> findByIdWithProjects(@Param("id") Long id);
	
	boolean existsByEmailIgnoreCase(String email);
	boolean existsByUsernameIgnoreCase(String username);
}
