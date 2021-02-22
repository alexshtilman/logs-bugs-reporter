/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.BugsApi.ASSIGN;
import static telran.logs.bugs.api.BugsApi.BUGS;
import static telran.logs.bugs.api.BugsApi.BY_ID;
import static telran.logs.bugs.api.BugsApi.OPEN;
import static telran.logs.bugs.api.BugsApi.PROGRAMMERS;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(BUGS)
@Log4j2
public class BugsController {

	BugsReporterImpl bugsReporterService;

	@Autowired
	public BugsController(BugsReporterImpl bugsReporterService) {
		super();
		this.bugsReporterService = bugsReporterService;
	}

	@GetMapping(PROGRAMMERS + BY_ID)
	public List<BugResponseDto> getBugsProgrammer(@PathVariable(name = "id") @NotBlank @Min(1) long programmerId) {
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
