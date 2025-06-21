package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.ProjectImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, ProjectImageId> {

}
