/**
 * 
 */
package telran.aop;

import java.rmi.ServerException;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

/**
 * @author Alex Shtilman Apr 16, 2021
 *
 */
@TestConfiguration
public class LoggingComponentConfig {
	@RestController
	public static class TestController {

		@GetMapping("/get")
		String getObject(@RequestParam(name = "id") String objectId) {
			return objectId + " found";
		}

		@PostMapping("/post")
		String postObject(@RequestBody LogDto dto) {
			return dto.getArtifact() + " added successfully";
		}

		@PutMapping("/put")
		String putObject(@RequestBody LogDto dto) {
			return dto.getArtifact() + " updated successfully";
		}

		@DeleteMapping("/delete")
		String deleteObject(@RequestParam(name = "id") String objectId) {
			return objectId + " deleted";
		}

		@GetMapping("/exeptions/get")
		String getExceptionObject(@RequestParam(name = "id") String objectId) {
			throw new IllegalArgumentException(objectId + " is incorrect!");
		}

		@PostMapping("/exeptions/post")
		String postExceptionObject(@RequestBody LogDto dto) {
			throw new DuplicatedException(dto.getArtifact() + " already exist");
		}

		@PutMapping("/exeptions/put")
		String putExceptionObject(@RequestBody LogDto dto) {
			throw new NotFoundException(dto.getArtifact() + " not found");
		}

		@DeleteMapping("/exeptions/delete")
		String deleteExceptionObject(@RequestParam(name = "id") String objectId) throws ServerException {
			throw new ServerException("can not delete object by id " + objectId);
		}

	}

	@Bean
	public LoggingComponentImpl loggingComponent() {

		return new LoggingComponentImpl();
	}

	@Bean
	public LoggerAspect getAspect() {
		return new LoggerAspect();
	}
}
