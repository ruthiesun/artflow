package com.artflow.artflow.repository;

import com.artflow.artflow.model.ProjectTag;
import com.artflow.artflow.model.ProjectTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {

}
