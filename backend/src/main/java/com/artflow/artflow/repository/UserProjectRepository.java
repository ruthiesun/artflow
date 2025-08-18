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
	List<UserProject> findByOwner_UsernameOrderByCreatedDateTimeDesc(String username);
	List<UserProject> findByOwner_UsernameAndVisibilityOrderByCreatedDateTimeDesc(String username, Visibility visibility);
	Optional<UserProject> findByOwner_UsernameAndProjectName(String username, String projectName);
	@Query("SELECT p FROM UserProject p LEFT JOIN FETCH p.images i WHERE p.id = :id ORDER BY i.position ASC")
	Optional<UserProject> findByIdWithImages(@Param("id") Long id);
	@Query("SELECT p FROM UserProject p LEFT JOIN FETCH p.projectTags WHERE p.id = :id")
	Optional<UserProject> findByIdWithTags(@Param("id") Long id);
	@Query("SELECT p FROM UserProject p LEFT JOIN FETCH p.projectTags WHERE p.projectName = :projectName AND p.owner.username = :username")
	Optional<UserProject> findByOwner_UsernameAndProjectNameWithTags(@Param("username") String username, @Param("projectName") String projectName);
	@Query("""
	SELECT DISTINCT p FROM UserProject p
	LEFT JOIN p.projectTags t
	WHERE p.owner.username = :ownerUsername
	AND t.tag.name IN :tags
	ORDER BY p.createdDateTime DESC
	""")
	List<UserProject> findByUsernameAndTagsOrderByCreatedDateTime(
			@Param("ownerUsername") String ownerUsername,
			@Param("tags") Set<String> tags
	);
	@Query("""
	SELECT DISTINCT p FROM UserProject p
	LEFT JOIN p.projectTags t
	WHERE p.owner.username = :ownerUsername
	AND p.visibility = :visibility
	AND t.tag.name IN :tags
	ORDER BY p.createdDateTime DESC
	""")
	List<UserProject> findByUsernameAndVisibilityAndTagsOrderByCreatedDateTime(
			@Param("ownerUsername") String ownerUsername,
			@Param("visibility") Visibility visibility,
			@Param("tags") Set<String> tags
	);

	
}
