package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {
	Optional<ProjectTag> findByTagNameAndProject_ProjectName(String tagName, String projectName);
	Optional<List<ProjectTag>> findByProject_Owner_Email(String userEmail);
	Optional<List<ProjectTag>> findByProject_ProjectName(String projectName);
}
