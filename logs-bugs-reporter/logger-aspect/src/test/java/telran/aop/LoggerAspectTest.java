/**
 * 
 */
package telran.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.rmi.ServerException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LoggerAspectTest.TestController.class)
@ContextConfiguration(classes = { LoggerAspectTest.TestController.class })
class LoggerAspectTest {

	public static @RestController class TestController {
		static LogDto logDtoExp = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");

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

	@Autowired
	MockMvc mock;

	ObjectMapper mapper = new ObjectMapper();

	public void postDtoWithResponceCode(int responceCode) throws Exception {
		assertEquals(responceCode,
				mock.perform(post("/post").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(TestController.logDtoExp))).andReturn().getResponse()
						.getStatus());
	}

	@Test
	void testNormal() throws Exception {
		postDtoWithResponceCode(200);
	}

}
