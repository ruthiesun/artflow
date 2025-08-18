package com.artflow.artflow.service;

import com.artflow.artflow.exception.ForbiddenActionException;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.model.User;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.model.Visibility;
import com.artflow.artflow.repository.UserProjectRepository;
import com.artflow.artflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VisibilityUtilService {
    private final UserProjectRepository projectRepo;
    private final UserRepository userRepo;
    
    public VisibilityUtilService(UserProjectRepository projectRepo, UserRepository userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }
    
    public void checkUsernameAgainstEmail(String email, String username) {
        if (!doesUsernameMatchEmail(email, username)) {
            throw new ForbiddenActionException();
        }
    }
    
    public boolean doesUsernameMatchEmail(String email, String username) {
        User user = userRepo.findByEmailWithProjects(email).get();
        return user.getUsername().equals(username);
    }
    
    public UserProject getProjectCheckUsernameAgainstProjectVisibility(String email, String username, String projectName) {
        boolean publicOnly = !doesUsernameMatchEmail(email, username);
        
        Optional<UserProject> project = projectRepo.findByOwner_UsernameAndProjectName(username, projectName);
        if (publicOnly && (!project.isPresent() || project.get().getVisibility() != Visibility.PUBLIC)) {
            throw new ForbiddenActionException();
        }
        if (!project.isPresent()) {
            throw new ProjectNotFoundException(projectName, username);
        }
        
        return project.get();
    }
    
    public void checkUsernameAgainstProjectVisibility(String email, String username, String projectName) {
        getProjectCheckUsernameAgainstProjectVisibility(email, username, projectName);
    }
}
