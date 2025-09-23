package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import com.artflow.artflow.model.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {
	Optional<ProjectTag> findByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(String tagName, String projectName, String username);
	boolean existsByTagNameIgnoreCaseAndProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(String tagName, String projectName, String username);
	@Query("""
	    SELECT DISTINCT pt.tag.name FROM ProjectTag pt
	    WHERE LOWER(pt.project.owner.username) = LOWER(:username)
	""")
	List<String> findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCase(@Param("username") String username);
	@Query("""
		SELECT DISTINCT pt.tag.name FROM ProjectTag pt
		LEFT JOIN pt.project p
		WHERE LOWER(p.owner.username) = LOWER(:username)
		AND p.visibility = :visibility
	""")
	List<String> findDistinctTagNameIgnoreCaseByProject_Owner_UsernameIgnoreCaseAndProject_Visibility(@Param("username") String username, @Param("visibility") Visibility visibility);
	List<ProjectTag> findByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(String projectName, String username);
}
