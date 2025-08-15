package com.artflow.artflow.service;

import com.artflow.artflow.dto.ProjectImageCreateDto;
import com.artflow.artflow.dto.ProjectImageDto;
import com.artflow.artflow.dto.ProjectImageUpdateDto;
import com.artflow.artflow.exception.ProjectImageNotFoundException;
import com.artflow.artflow.exception.ProjectNotFoundException;
import com.artflow.artflow.model.ProjectImage;
import com.artflow.artflow.model.UserProject;
import com.artflow.artflow.repository.ProjectImageRepository;
import com.artflow.artflow.repository.UserProjectRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectImageService {
	private static final Logger log = LoggerFactory.getLogger(ProjectImageService.class);
	private final UserProjectRepository projectRepo;
	private final ProjectImageRepository projectImageRepo;
	
	public ProjectImageService(UserProjectRepository projectRepo, ProjectImageRepository projectImageRepo) {
		this.projectRepo = projectRepo;
		this.projectImageRepo = projectImageRepo;
	}
	
	@Transactional
	public ProjectImageDto create(String projectName, ProjectImageCreateDto projectImageCreateDto, String email) {
		UserProject project = projectRepo.findByOwner_EmailAndProjectName(email, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, email));
		long currentNumImages = projectImageRepo.countByProject_ProjectNameAndProject_Owner_Email(
				projectName,
				email
		);
		log.info("creating new image with position=" + currentNumImages);
		ProjectImage image = new ProjectImage(
				(int) currentNumImages,
				projectImageCreateDto.getCaption(),
				projectImageCreateDto.getDateTime(),
				projectImageCreateDto.getUrl(),
				project
		);
		project.getImages().add(image);
		projectImageRepo.save(image);
		project.updateDateTime();
		return toDto(image);
	}
	
	public List<ProjectImageDto> getImagesForProject(String projectName, String email) {
		projectRepo.findByOwner_EmailAndProjectName(email, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, email));
		List<ProjectImage> images = projectImageRepo.findByProject_ProjectNameAndProject_Owner_EmailOrderByPosition(projectName, email);
		return toDto(images);
	}
	
	public ProjectImageDto getImageForProject(String projectName, Long imageId, String email) {
		projectRepo.findByOwner_EmailAndProjectName(email, projectName)
				.orElseThrow(() -> new ProjectNotFoundException(projectName, email));
		ProjectImage image = projectImageRepo
				.findById(imageId)
				.orElseThrow(() -> new ProjectImageNotFoundException(imageId));
		return toDto(image);
	}
	
	@Transactional
	public ProjectImageDto updateProjectImage(ProjectImageUpdateDto projectImageUpdateDto) {
		ProjectImage image = projectImageRepo
				.findById(projectImageUpdateDto.getId())
				.orElseThrow(() -> new ProjectImageNotFoundException(projectImageUpdateDto.getId()));
		image.setUrl(projectImageUpdateDto.getUrl());
		image.setDateTime(projectImageUpdateDto.getDateTime());
		image.setCaption(projectImageUpdateDto.getCaption());
		int newPos = projectImageUpdateDto.getPosition();
		int currPos = image.getPosition();

		if (newPos != currPos) {
			log.info("updateProjectImage: about to update image from pos " + currPos + " to " + newPos);
			UserProject project = image.getProject();
			int numImagesInProject = project.getImages().size();
			if (newPos < currPos) {
				image.setPosition(-1);
				log.info("set image pos to temp pos=" + image.getPosition());
				project.getImages().sort(Comparator.comparing(ProjectImage::getPosition));
				updateProjectImagePositionsDescending(project.getImages(), newPos + 1, currPos + 1);
			}
			else {
				image.setPosition(numImagesInProject);
				log.info("set image pos to temp pos=" + image.getPosition());
				project.getImages().sort(Comparator.comparing(ProjectImage::getPosition));
				updateProjectImagePositionsAscending(project.getImages(), currPos, newPos); // images are lazily loaded, so the temp pos does not cause a conflict, and the old pos is now free
			}
			projectRepo.flush(); // need this for the db to know that the new pos is now free. above: did not need flush because the lazy load implicitly flushes (this is my hypothesis)
			image.setPosition(newPos);
		}
		
		image.getProject().updateDateTime();
		return toDto(image);
	}
	
	@Transactional
	public void deleteProjectImage(Long imageId) {
		Optional<ProjectImage> image = projectImageRepo.findById(imageId);
		if (image.isPresent()) {
			int pos = image.get().getPosition();
			log.info("deleting image at pos " + pos);
			UserProject project = projectRepo.findByIdWithImages(image.get().getProject().getId()).get();
//			UserProject project = image.get().getProject(); // removal with this doesn't get flushed
			project.getImages().remove(pos);
			projectRepo.flush(); // need the removal to propagate to images and make the removed position available again
			updateProjectImagePositionsAscending(project.getImages(), pos, project.getImages().size());
			image.get().getProject().updateDateTime();
		}
	}
	
	private void updateProjectImagePositionsAscending(List<ProjectImage> images, int startIndex, int endIndex) {
		log.info("updating image positions from " + startIndex + " to " + (endIndex - 1) + " (inclusive):");
		for (int i = startIndex; i < endIndex; i++) {
			ProjectImage image = images.get(i);
			log.info("updating position from " + images.get(i).getPosition() + " to " + i);
			image.setPosition(i);
		}
	}
	
	private void updateProjectImagePositionsDescending(List<ProjectImage> images, int startIndex, int endIndex) {
		log.info("updating image positions from " + (endIndex - 1) + " to " + startIndex + " (inclusive):");
		for (int i = endIndex - 1; i >= startIndex ; i--) {
			ProjectImage image = images.get(i);
			log.info("updating position from " + images.get(i).getPosition() + " to " + i);
			projectImageRepo.flush(); //for some reason, we need a flush here but not for the ascending function
			image.setPosition(i);
		}
	}
	
	private List<ProjectImageDto> toDto(List<ProjectImage> projectImages) {
		List<ProjectImageDto> projectImageDtos = new ArrayList<>();
		for (ProjectImage image : projectImages) {
			projectImageDtos.add(toDto(image));
		}
		return projectImageDtos;
	}
	
	private ProjectImageDto toDto(ProjectImage image) {
		return new ProjectImageDto(
				image.getId(),
				image.getPosition(),
				image.getCaption(),
				image.getDateTime(),
				image.getUrl(),
				image.getProject().getProjectName()
		);
	}
}
