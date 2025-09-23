package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
	long countByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCase(String projectName, String username);
	Optional<ProjectImage> findByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCaseAndPosition(String projectName, String username, int position);
	List<ProjectImage> findByProject_ProjectNameIgnoreCaseAndProject_Owner_UsernameIgnoreCaseOrderByPosition(String projectName, String username);
}
