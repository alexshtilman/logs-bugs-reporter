/**
 * 
 */
package telran.logs.bugs.services;

import java.util.List;

import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public interface BugsReporter {

	ProgrammerDto addProgrammerDto(ProgrammerDto programmerDto);

	ArtifactDto addArtifactDto(ArtifactDto artifactDto);

	BugResponseDto openBug(BugDto bugDto);

	BugResponseDto openAndAssignBug(BugAssignDto bugDto);

	void assginBug(AssignBugData assgnData);

	void closeBug(CloseBugData closeData);

	List<BugResponseDto> getNonAssignedBugs();

	List<BugResponseDto> getUnclosedBugsMoreDuration(int days);

	List<BugResponseDto> getBugsByProgrammerId(long programmerId);

	List<EmailBugsCount> getEmailBugsCounts();

	List<String> getProgrammersMostBugs(int nProgrammers);

	List<String> getProgrammersLeastBugs(int nProgrammers);

	List<SeriousnessBugCount> getSeriousnessBugCounts();

	List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes);
}
