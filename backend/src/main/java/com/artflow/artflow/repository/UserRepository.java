package com.artflow.artflow.repository;

import com.artflow.artflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.email = :email")
	Optional<User> findByEmailWithProjects(@Param("email") String email);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.id = :id")
	Optional<User> findByIdWithProjects(@Param("id") Long id);
	
	boolean existsByEmail(String email);
	boolean existsByUsername(String username);
}
