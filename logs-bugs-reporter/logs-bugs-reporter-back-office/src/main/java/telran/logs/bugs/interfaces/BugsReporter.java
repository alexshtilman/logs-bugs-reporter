/**
 * 
 */
package telran.logs.bugs.interfaces;

import java.util.List;

import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponceDto;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.ProgrammerDto;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface BugsReporter {

	ProgrammerDto addProgrammerDto(ProgrammerDto programmerDto);

	ArtifactDto addArtifactDto(ArtifactDto artifactDto);

	BugResponceDto openBug(BugDto bugDto);

	BugResponceDto openAndAssignBug(BugAssignDto bugDto);

	void assginBug(AssignBugData assgnData);

	void closeBug(CloseBugData closeData);

	List<BugResponceDto> getNonAssignedBugs();

	List<BugResponceDto> getUnclosedBugsMoreDuration(int days);

	List<BugResponceDto> getBugsByProgrammerId(long programmerId);

	List<EmailBugsCount> getEmailBugsCounts();

	List<String> getProgrammersMostBugs(int nProgrammers);

	List<String> getProgrammersLeastBugs(int nProgrammers);
}
