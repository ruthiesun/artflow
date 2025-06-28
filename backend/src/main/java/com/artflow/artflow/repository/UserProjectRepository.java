package com.artflow.artflow.repository;

import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
	List<UserProject> findByOwner_Email(String email);
	List<UserProject> findByOwner_EmailAndVisibility(String email, Visibility visibility);
	Optional<UserProject> findByOwner_EmailAndProjectName(String email, String projectName);
	@Query("SELECT p FROM UserProject p LEFT JOIN FETCH p.images i WHERE p.id = :id ORDER BY i.position ASC")
	Optional<UserProject> findByIdWithImages(@Param("id") Long id);
	@Query("SELECT p FROM UserProject p LEFT JOIN FETCH p.projectTags WHERE p.id = :id")
	Optional<UserProject> findByIdWithTags(@Param("id") Long id);
	@Query("""
    SELECT DISTINCT p FROM UserProject p
    LEFT JOIN p.projectTags t
    WHERE p.owner.email = :ownerEmail
	AND t.tag.name IN :tags
	""")
	List<UserProject> findByEmailAndTags(
			@Param("ownerEmail") String ownerEmail,
			@Param("tags") Set<String> tags
	);
	@Query("""
    SELECT DISTINCT p FROM UserProject p
    LEFT JOIN p.projectTags t
    WHERE p.owner.email = :ownerEmail
    AND p.visibility = :visibility
	AND t.tag.name IN :tags
	""")
	List<UserProject> findByEmailAndVisibilityAndTags(
			@Param("ownerEmail") String ownerEmail,
			@Param("visibility") Visibility visibility,
			@Param("tags") Set<String> tags
	);

	
}
