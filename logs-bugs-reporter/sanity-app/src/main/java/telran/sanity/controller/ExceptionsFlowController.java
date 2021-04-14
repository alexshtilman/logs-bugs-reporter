/**
 * 
 */
package telran.sanity.controller;

import java.rmi.ServerException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.sanity.dto.FlowDto;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@RestController
@RequestMapping("/exeptions")
public class ExceptionsFlowController {

	@GetMapping("/get")
	String getObject(@RequestParam(name = "id") String objectId) {
		throw new IllegalArgumentException(objectId + " is incorrect!");
	}

	@PostMapping("/post")
	String postObject(@RequestBody FlowDto dto) {
		throw new DuplicatedException(dto.getName() + " already exist");
	}

	@PutMapping("/put")
	String putObject(@RequestBody FlowDto dto) {
		throw new NotFoundException(dto.getName() + " not found");
	}

	@DeleteMapping("/delete")
	String deleteObject(@RequestParam(name = "id") String objectId) throws ServerException {
		throw new ServerException("can not delete object by id " + objectId);
	}
}
