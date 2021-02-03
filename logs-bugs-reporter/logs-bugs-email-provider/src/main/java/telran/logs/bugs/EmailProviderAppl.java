package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.jpa.entities.Artifact;

@SpringBootApplication
@RestController
@RequestMapping("/email")
public class EmailProviderAppl {

	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);
	}

	@Autowired
	ArtifactRepo artifactRepo;

	@GetMapping("/{artifact}")
	public String getEmailFromAtifact(@PathVariable(name = "artifact") String artifact) {
		Artifact artifactDb = artifactRepo.findById(artifact).orElse(null);
		return artifactDb == null ? "" : artifactDb.getProgrammer().getEmail();
	}
}
