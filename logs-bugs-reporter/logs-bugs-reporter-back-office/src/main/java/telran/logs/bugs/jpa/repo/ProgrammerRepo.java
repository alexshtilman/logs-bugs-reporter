/**
 * 
 */
package telran.logs.bugs.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Programmer;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface ProgrammerRepo extends JpaRepository<Programmer, Long> {

}
