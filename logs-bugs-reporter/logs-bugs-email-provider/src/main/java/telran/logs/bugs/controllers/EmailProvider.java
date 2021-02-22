/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.Constants.ARTIFACT;
import static telran.logs.bugs.api.Constants.MAIL_CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactRepository;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@RestController
@RequestMapping(MAIL_CONTROLLER)
@Log4j2
public class EmailProvider {
	@Autowired
	ArtifactRepository artifactRepo;

	@GetMapping(ARTIFACT)
	public String getEmailFromAtifact(@PathVariable(name = "artifact") String artifact) {
		Artifact artifactDb = artifactRepo.findById(artifact).orElse(null);
		log.debug("found artifactDb={}", artifactDb);
		return artifactDb == null ? "" : artifactDb.getProgrammer().getEmail();
	}
}
