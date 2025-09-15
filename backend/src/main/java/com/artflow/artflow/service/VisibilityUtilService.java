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
    
    public void checkUsernameAgainstId(Long id, String username) {
        if (!doesUsernameBelongToId(id, username)) {
            throw new ForbiddenActionException();
        }
    }
    
    public boolean doesUsernameBelongToId(Long id, String username) {
        if (id == null) {
            return false;
        }
        
        User user = userRepo.findById(id).get();
        return user.getUsername().equals(username);
    }
    
    public UserProject getProjectCheckUsernameAgainstProjectVisibility(Long id, String username, String projectName) {
        boolean publicOnly = !doesUsernameBelongToId(id, username);
        
        Optional<UserProject> project = projectRepo.findByOwner_UsernameAndProjectName(username, projectName);
        if (publicOnly && (!project.isPresent() || project.get().getVisibility() != Visibility.PUBLIC)) {
            throw new ForbiddenActionException();
        }
        if (!project.isPresent()) {
            throw new ProjectNotFoundException(projectName, username);
        }
        
        return project.get();
    }
    
    public void checkUsernameAgainstProjectVisibility(Long id, String username, String projectName) {
        getProjectCheckUsernameAgainstProjectVisibility(id, username, projectName);
    }
}
