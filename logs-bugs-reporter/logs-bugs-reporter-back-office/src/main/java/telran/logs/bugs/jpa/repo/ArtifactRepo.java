/**
 * 
 */
package telran.logs.bugs.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Artifact;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface ArtifactRepo extends JpaRepository<Artifact, String> {

}
