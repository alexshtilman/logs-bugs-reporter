/**
 * 
 */
package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.jpa.entities.Bug;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface BugRepo extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus status);

	List<Bug> findByStatusNotAndDateOppenBefore(BugStatus closed, LocalDate dateOpen);

	@Query("SELECT programmer.email as email, count(b) as count FROM Bug b RIGHT JOIN b.programmer programmer GROUP BY programmer ORDER BY count(b) DESC")
	List<EmailBugsCount> groupByEmailAndCount();

}
