/**
 * 
 */
package telran.logs.bugs.services;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
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
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.logs.bugs.jpa.entities.Artifact;
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
@Log4j2
public class BugsReporterImpl implements BugsReporter {

	private static final String FOUND_BUGS = "found bugs {}";
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
		Programmer programmer = programmerRepo.findById(programmerDto.id).orElse(null);
		if (programmer != null) {
			throw new DuplicatedException(String.format("Programmer with id %s not found!", programmerDto.id));
		}
		programmerRepo.save(programmer);
		return programmerDto;
	}

	@Transactional
	@Override
	public ArtifactDto addArtifactDto(ArtifactDto artifactDto) {
		Programmer programmer = programmerRepo.findById(artifactDto.getProgrammerId()).orElse(null);
		if (programmer == null) {
			throw new NotFoundException(
					String.format("Programmer with id %s not found!", artifactDto.getProgrammerId()));
		}
		Artifact artifact = artifactRepo.findById(artifactDto.getArtifactId()).orElse(null);
		if (artifact != null) {
			throw new DuplicatedException(
					String.format("Artifact with id %s already exist!", artifactDto.getArtifactId()));
		}
		artifactRepo.save(new Artifact(artifactDto.getArtifactId(), programmer));
		return artifactDto;
	}

	@Transactional
	@Override
	public BugResponseDto openBug(BugDto bugDto) {
		LocalDate dateOpen = LocalDate.now();
		if (bugDto.dateOpen != null) {
			dateOpen = bugDto.dateOpen;
		}
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.OPEND, bugDto.seriousness,
				OpenningMethod.MANUAL, null);
		bugsRepo.save(bug);
		return toBugResponceDto(bug);
	}

	@Transactional
	@Override
	public BugResponseDto openAndAssignBug(BugAssignDto bugDto) {

		Programmer programmer = programmerRepo.findById(bugDto.programmerId).orElse(null);
		if (programmer == null) {
			throw new NotFoundException(String.format("Programmer with id %s not found", bugDto.programmerId));
		}
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
		Bug bug = bugsRepo.findById(assgnData.bugId).orElse(null);
		if (bug == null) {
			throw new NotFoundException(String.format("Bug not found by id %s", assgnData.bugId));
		}
		bug.setDescription(bug.getDescription() + "%n Assigment Description " + assgnData.description);
		Programmer programmer = programmerRepo.findById(assgnData.programmerId).orElse(null);
		bug.setProgrammer(programmer);
		bug.setStatus(BugStatus.ASSIGNED);

	}

	@Transactional
	@Override
	public void closeBug(CloseBugData closeData) {
		LocalDate dateClose = LocalDate.now();
		if (closeData.dateClose != null) {
			dateClose = closeData.dateClose;
		}
		Bug bug = bugsRepo.findById(closeData.bugId).orElse(null);
		if (bug == null) {
			throw new NotFoundException(String.format("Bug not found by id %s", closeData.bugId));
		}
		bug.setDescription(String.format("%s%nbug was closed %s because: %s", bug.getDescription(), dateClose,
				closeData.description));
		bug.setDateClose(dateClose);
		bug.setStatus(BugStatus.CLOSED);
	}

	@Override
	public List<BugResponseDto> getNonAssignedBugs() {
		List<Bug> bugs = bugsRepo.findByStatus(BugStatus.OPEND);
		return toListBugResponceDto(bugs);
	}

	@Override
	public List<BugResponseDto> getUnclosedBugsMoreDuration(int days) {
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugsRepo.findByStatusNotAndDateOppenBefore(BugStatus.CLOSED, dateOpen);

		return toListBugResponceDto(bugs);
	}

	@Override
	public List<BugResponseDto> getBugsByProgrammerId(long programmerId) {
		List<Bug> bugs = bugsRepo.findByProgrammerId(programmerId);

		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponceDto(bugs);
	}

	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		List<EmailBugsCount> bugs = bugsRepo.groupByEmailAndCount();
		bugs.forEach(bug -> log.debug(FOUND_BUGS, bugs));
		return bugs;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammers) {
		List<String> names = bugsRepo.findProgrammersBugsDesc(PageRequest.of(0, nProgrammers));
		names.forEach(name -> log.debug(FOUND_BUGS, name));
		return names;
	}

	@Override
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		List<String> names = bugsRepo.findProgrammersBugsAsc(PageRequest.of(0, nProgrammers));
		names.forEach(name -> log.debug(FOUND_BUGS, name));
		return names;
	}

	private BugResponseDto toBugResponceDto(Bug bug) {
		Programmer programmer = bug.getProgrammer();
		long programmerId = 0;
		if (programmer != null) {
			programmerId = programmer.getId();
		}
		return new BugResponseDto(bug.getSeriousness(), bug.getDescription(), bug.getDateOppen(), programmerId,
				bug.getDateClose(), bug.getStatus(), bug.getOppeningMethod(), bug.getId());
	}

	private List<BugResponseDto> toListBugResponceDto(List<Bug> bugs) {
		bugs.forEach(bug -> log.debug(FOUND_BUGS, bug));
		return bugs.stream().map(this::toBugResponceDto).collect(Collectors.toList());
	}

	@Override
	public List<SeriousnessBugCount> getSeriousnessBugCounts() {
		List<SeriousnessBugCount> bugs = bugsRepo.getSeriousnessBugCounts();
		bugs.forEach(bug -> log.debug(FOUND_BUGS, bug));
		return bugs;
	}

	@Override
	public List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes) {
		if (nTypes < 0) {
			throw new ConstraintViolationException(String.format("nTypes shold be grather than 0!, but was %s", nTypes),
					null);
		}
		List<Seriousness> bugs = bugsRepo.getSeriousnessTypesWithMostBugs(PageRequest.of(0, nTypes));
		bugs.forEach(bug -> log.debug(FOUND_BUGS, bug));
		return bugs;
	}
}
