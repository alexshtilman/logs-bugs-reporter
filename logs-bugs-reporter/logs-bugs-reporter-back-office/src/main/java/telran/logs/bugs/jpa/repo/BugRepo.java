/**
 * 
 */
package telran.logs.bugs.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Bug;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface BugRepo extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

}
