package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {
	Optional<ProjectTag> findByTagNameAndProject_ProjectNameAndProject_Owner_Email(String tagName, String projectName, String email);
	boolean existsByTagNameAndProject_ProjectNameAndProject_Owner_Email(String tagName, String projectName, String email);
	Optional<ProjectTag> findByProject_IdAndProject_Owner_Email(Long projectId, String email);
	@Query("""
	    SELECT DISTINCT pt.tag.name FROM ProjectTag pt
	    WHERE pt.project.owner.email = :userEmail
	""")
	List<String> findDistinctTagNameByProject_Owner_Email(@Param("userEmail") String userEmail);
	List<ProjectTag> findByProject_ProjectNameAndProject_Owner_Email(String projectName, String email);
	List<ProjectTag> findByTag_Name(String tagName);
	long countByTag_Id(Long id);
}
