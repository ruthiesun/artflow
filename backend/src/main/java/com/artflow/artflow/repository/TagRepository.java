package com.artflow.artflow.repository;

import com.artflow.artflow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByNameIgnoreCase(String name);
}
