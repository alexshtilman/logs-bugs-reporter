package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactRepository;

@SpringBootApplication
@RestController
@RequestMapping("/email")
@Log4j2
public class EmailProviderAppl {

	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);
	}

	@Autowired
	ArtifactRepository artifactRepo;

	@GetMapping("/{artifact}")
	public String getEmailFromAtifact(@PathVariable(name = "artifact") String artifact) {
		Artifact artifactDb = artifactRepo.findById(artifact).orElse(null);
		log.debug("found artifactDb={}", artifactDb);
		return artifactDb == null ? "" : artifactDb.getProgrammer().getEmail();
	}
}
