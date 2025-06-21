package com.artflow.artflow.repository;

import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {

}
