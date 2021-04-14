/**
 * 
 */
package telran.sanity.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.sanity.dto.FlowDto;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@RestController
@RequestMapping("/normal")
public class NormalFlowController {

	@GetMapping("/get")
	String getObject(@RequestParam(name = "id") String objectId) {
		return objectId + " found";
	}

	@PostMapping("/post")
	String postObject(@RequestBody FlowDto dto) {
		return dto.getId() + " added successfully";
	}

	@PutMapping("/put")
	String putObject(@RequestBody FlowDto dto) {
		return dto.getId() + " updated successfully";
	}

	@DeleteMapping("/delete")
	String deleteObject(@RequestParam(name = "id") String objectId) {
		return objectId + " deleted";
	}
}
