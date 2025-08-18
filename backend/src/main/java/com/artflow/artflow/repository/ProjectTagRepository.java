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
import java.util.Set;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {
	Optional<ProjectTag> findByTagNameAndProject_ProjectNameAndProject_Owner_Username(String tagName, String projectName, String username);
	boolean existsByTagNameAndProject_ProjectNameAndProject_Owner_Username(String tagName, String projectName, String username);
	@Query("""
	    SELECT DISTINCT pt.tag.name FROM ProjectTag pt
	    WHERE pt.project.owner.username = :username
	""")
	List<String> findDistinctTagNameByProject_Owner_Username(@Param("username") String username);
	@Query("""
		SELECT DISTINCT pt.tag.name FROM ProjectTag pt
		LEFT JOIN pt.project p
		WHERE p.owner.username = :username
		AND p.visibility = :visibility
	""")
	List<String> findDistinctTagNameByProject_Owner_UsernameAndProject_Visibility(@Param("username") String username, @Param("visibility") Visibility visibility);
	List<ProjectTag> findByProject_ProjectNameAndProject_Owner_Username(String projectName, String username);
	List<ProjectTag> findByTag_Name(String tagName);
	long countByTag_Id(Long id);
}
