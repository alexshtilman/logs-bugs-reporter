/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.Constants.ASSIGN;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.ID;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;

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
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.ProgrammerDto;
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

	@PutMapping(ASSIGN)
	public void assignBug(@RequestBody @Valid AssignBugData assignData) {
		bugsReporterService.assginBug(assignData);
	}

}
