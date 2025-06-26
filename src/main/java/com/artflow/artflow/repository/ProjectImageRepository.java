package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
	long countByProject_ProjectNameAndProject_Owner_Email(String projectName, String email);
	Optional<ProjectImage> findByProject_ProjectNameAndProject_Owner_EmailAndPosition(String projectName, String email, int position);
	List<ProjectImage> findByProject_ProjectNameAndProject_Owner_Email(String projectName, String email);
	List<ProjectImage> findByProject_ProjectNameAndProject_Owner_EmailOrderByPosition(String projectName, String email);
}
