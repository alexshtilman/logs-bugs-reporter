package telran.logs.bugs;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Artifact;

public interface ArtifactRepo extends JpaRepository<Artifact, String> {

}
