package com.artflow.artflow.repository;

import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
	Optional<List<UserProject>> findByOwner_Email(String email);
	Optional<List<UserProject>> findByOwner_EmailAndVisibility(String email, Visibility visibility);
	Optional<UserProject> findByOwner_EmailAndProjectName(String email, String projectName);
	Optional<UserProject> findByOwner_EmailAndId(String email, Long id);
}
