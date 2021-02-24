/**
 * 
 */
package telran.logs.bugs.services;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepo;
import telran.logs.bugs.jpa.repo.BugRepo;
import telran.logs.bugs.jpa.repo.ProgrammerRepo;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@Service
public class BugsReporterImpl implements BugsReporter {

	BugRepo bugsRepo;
	ArtifactRepo artifactRepo;
	ProgrammerRepo programmerRepo;

	@Autowired
	public BugsReporterImpl(BugRepo bugsRepo, ArtifactRepo artifactRepo, ProgrammerRepo programmerRepo) {
		this.bugsRepo = bugsRepo;
		this.artifactRepo = artifactRepo;
		this.programmerRepo = programmerRepo;
	}

	@Transactional
	@Override
	public ProgrammerDto addProgrammerDto(ProgrammerDto programmerDto) {
		// FIXME add exception implementation handler
		programmerRepo.save(new Programmer(programmerDto.id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}

	@Override
	public ArtifactDto addArtifactDto(ArtifactDto artifactDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public BugResponseDto openBug(BugDto bugDto) {
		// FIXME exceptions
		LocalDate dateOpen = LocalDate.now();
		if (bugDto.dateOpen != null) {
			dateOpen = bugDto.dateOpen;
		}
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.OPEND, bugDto.seriousness,
				OpenningMethod.MANUAL, null);
		bugsRepo.save(bug);
		return toBugResponceDto(bug);
	}

	/**
	 * @param bug
	 * @return
	 */
	private BugResponseDto toBugResponceDto(Bug bug) {
		Programmer programmer = bug.getProgrammer();
		long programmerId = 0;
		if (programmer != null) {
			programmer.getId();
		}
		return new BugResponseDto(bug.getSeriosness(), bug.getDescription(), bug.getDateOppen(), programmerId,
				bug.getDateClose(), bug.getStatus(), bug.getOppeningMethod(), bug.getId());
	}

	@Transactional
	@Override
	public BugResponseDto openAndAssignBug(BugAssignDto bugDto) {
		// FIXME exceptions
		Programmer programmer = programmerRepo.findById(bugDto.programmerId).orElse(null);
		// TODO EXCEPTION
		LocalDate dateOpen = LocalDate.now();
		if (bugDto.dateOpen != null) {
			dateOpen = bugDto.dateOpen;
		}
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED, bugDto.seriousness,
				OpenningMethod.MANUAL, programmer);
		bug = bugsRepo.save(bug);

		return toBugResponceDto(bug);
	}

	@Transactional
	@Override
	public void assginBug(AssignBugData assgnData) {
		// FIXME exceptions
		Bug bug = bugsRepo.findById(assgnData.bugId).orElse(null);
		bug.setDescription(bug.getDescription() + "%n Assigment Description " + assgnData.description);
		Programmer programmer = programmerRepo.findById(assgnData.programmerId).orElse(null);
		bug.setProgrammer(programmer);
		bug.setStatus(BugStatus.ASSIGNED);

	}

	@Override
	public void closeBug(CloseBugData closeData) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<BugResponseDto> getNonAssignedBugs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BugResponseDto> getUnclosedBugsMoreDuration(int days) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BugResponseDto> getBugsByProgrammerId(long programmerId) {
		List<Bug> bugs = bugsRepo.findByProgrammerId(programmerId);

		return bugs.isEmpty() ? new LinkedList<BugResponseDto>() : toListBugResponceDto(bugs);
	}

	private List<BugResponseDto> toListBugResponceDto(List<Bug> bugs) {
		return bugs.stream().map(this::toBugResponceDto).collect(Collectors.toList());
	}

	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		// TODO Auto-generated method stub
		return null;
	}

}
