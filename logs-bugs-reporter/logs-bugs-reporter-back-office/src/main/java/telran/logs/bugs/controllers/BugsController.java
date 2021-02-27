/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.Constants.ARTIFACTS;
import static telran.logs.bugs.api.Constants.ASSIGN;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.CLOSE;
import static telran.logs.bugs.api.Constants.EMAIL_BUGS_COUNTS;
import static telran.logs.bugs.api.Constants.ID;
import static telran.logs.bugs.api.Constants.LEAST_BUGS;
import static telran.logs.bugs.api.Constants.MOST_BUGS;
import static telran.logs.bugs.api.Constants.NON_ASSIGNED_BUGS_COUNTS;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;
import static telran.logs.bugs.api.Constants.SERIOSNESS_BUGS_COUNT;
import static telran.logs.bugs.api.Constants.TYPES_BUGS_COUNT;
import static telran.logs.bugs.api.Constants.UNCLOSED_DURATION;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.ProgrammerName;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.services.BugsReporterImpl;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@RestController
@RequestMapping(BUGS_CONTROLLER)
@Log4j2
@Validated
public class BugsController {

	BugsReporterImpl bugsReporterService;

	@Autowired
	public BugsController(BugsReporterImpl bugsReporterService) {
		super();
		this.bugsReporterService = bugsReporterService;
	}

	@GetMapping(PROGRAMMERS + ID)
	public List<BugResponseDto> getBugsProgrammer(@PathVariable(name = "id") @Min(1) long programmerId) {
		List<BugResponseDto> dto = bugsReporterService.getBugsByProgrammerId(programmerId);
		log.debug("got list BugResponseDto.size = {}", dto.size());
		return dto;
	}

	@PostMapping(PROGRAMMERS)
	public ProgrammerDto addProgrammer(@RequestBody @Valid ProgrammerDto programmerDto) {
		ProgrammerDto dto = bugsReporterService.addProgrammerDto(programmerDto);
		log.debug("got ProgrammerDto {}", dto);
		return dto;
	}

	@PostMapping(OPEN)
	public BugResponseDto openBug(@RequestBody @Valid BugDto bugDto) {
		BugResponseDto dto = bugsReporterService.openBug(bugDto);
		log.debug("got BugResponseDto {}", dto);
		return dto;
	}

	@PostMapping(OPEN + ASSIGN)
	public BugResponseDto openAndAssignBug(@RequestBody @Valid BugAssignDto bugAssignDto) {
		BugResponseDto dto = bugsReporterService.openAndAssignBug(bugAssignDto);
		log.debug("got BugResponseDto {}", dto);
		return dto;
	}

	@PostMapping(ARTIFACTS)
	public ArtifactDto addArtifact(@RequestBody @Valid ArtifactDto drtifactDto) {
		ArtifactDto dto = bugsReporterService.addArtifactDto(drtifactDto);
		log.debug("got ArtifactDto {}", dto);
		return dto;
	}

	@PutMapping(ASSIGN)
	public void assignBug(@RequestBody @Valid AssignBugData assignData) {
		bugsReporterService.assginBug(assignData);
	}

	@PutMapping(CLOSE)
	public void closeBug(@RequestBody @Valid CloseBugData closeData) {
		bugsReporterService.closeBug(closeData);
	}

	@GetMapping(EMAIL_BUGS_COUNTS)
	public List<EmailBugsCount> getEmailBugsCounts() {
		return bugsReporterService.getEmailBugsCounts();
	}

	@GetMapping(NON_ASSIGNED_BUGS_COUNTS)
	public List<BugResponseDto> getNonAssignedBugs() {
		return bugsReporterService.getNonAssignedBugs();
	}

	@GetMapping(UNCLOSED_DURATION)
	public List<BugResponseDto> getUnclosedBugsMoreDuration(@RequestParam(name = "days") @Min(1) int days) {
		return bugsReporterService.getUnclosedBugsMoreDuration(days);
	}

	@GetMapping(MOST_BUGS)
	public List<ProgrammerName> getProgrammersMostBugs(@RequestParam(name = "limit") @Min(0) int limit) {
		return bugsReporterService.getProgrammersMostBugs(limit);
	}

	@GetMapping(LEAST_BUGS)
	public List<ProgrammerName> getProgrammersLeastBugs(@RequestParam(name = "limit") @Min(0) int limit) {
		return bugsReporterService.getProgrammersLeastBugs(limit);
	}

	@GetMapping(SERIOSNESS_BUGS_COUNT)
	public List<SeriousnessBugCount> getSeriousnessBugCounts() {
		return bugsReporterService.getSeriousnessBugCounts();
	}

	@GetMapping(TYPES_BUGS_COUNT)
	public List<Seriousness> getSeriousnessTypesWithMostBugs(@RequestParam(name = "limit") @Min(0) int limit) {
		return bugsReporterService.getSeriousnessTypesWithMostBugs(limit);
	}
}
