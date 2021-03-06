/**
 * 
 */
package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.jpa.entities.Bug;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface BugRepo extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus status);

	List<Bug> findByStatusNotAndDateOppenBefore(BugStatus closed, LocalDate dateOpen);

	@Query("SELECT programmer.email as email, count(b) as count FROM Bug b RIGHT JOIN "
			+ "b.programmer programmer GROUP BY programmer ORDER BY count(b) DESC, programmer.name DESC")
	List<EmailBugsCount> groupByEmailAndCount();

	@Query(value = "SELECT programmer.name as name FROM Bug b "
			+ "GROUP BY programmer.name ORDER BY count(b) DESC, programmer.name ASC")
	List<String> findProgrammersBugsDesc(Pageable pageable);

	@Query(value = "SELECT programmer.name as name FROM Bug b RIGHT JOIN "
			+ "b.programmer programmer GROUP BY programmer ORDER BY count(b) ASC, programmer.name ASC")
	List<String> findProgrammersBugsAsc(Pageable pageable);

	@Query(value = "SELECT b.seriousness as seriousness , count(b) as count FROM Bug b "
			+ "GROUP BY seriousness ORDER BY count(b) DESC,b.seriousness ASC")
	List<SeriousnessBugCount> getSeriousnessBugCounts();

	@Query(value = "SELECT b.seriousness as seriousness  FROM Bug b "
			+ "GROUP BY seriousness ORDER BY count(b) DESC, b.seriousness ASC")
	List<Seriousness> getSeriousnessTypesWithMostBugs(Pageable pageable);

	long countBySeriousness(Seriousness s);
}
