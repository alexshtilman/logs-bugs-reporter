package telran.logs.bugs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;

import javax.validation.Valid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LogDtoTest.TestController.class)
@ContextConfiguration(classes = LogDtoTest.TestController.class)
class LogDtoTest {
	public static @RestController class TestController {
		static LogDto logDtoExp = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");

		@PostMapping("/")
		void testPost(@RequestBody @Valid LogDto logDto) {
			assertEquals(logDtoExp, logDto);
		}
	}

	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	MockMvc mock;

	@BeforeEach
	public void setup() {
		TestController.logDtoExp.dateTime = new Date();
		TestController.logDtoExp.logType = LogType.NO_EXCEPTION;
		TestController.logDtoExp.artifact = "artifact";
		TestController.logDtoExp.responseTime = 0;
		TestController.logDtoExp.result = "";
	}

	public void postDtoWithResponceCode(int responceCode) throws Exception {
		assertEquals(responceCode,
				mock.perform(post("/").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(TestController.logDtoExp))).andReturn().getResponse()
						.getStatus());
	}

	@Test
	void testNormal() throws Exception {
		postDtoWithResponceCode(200);
	}

	@Nested
	class faildTests {
		@DisplayName("dateTime = null")
		@Test
		void testArifactDateNull() throws Exception {
			TestController.logDtoExp.dateTime = null;
			postDtoWithResponceCode(400);
		}

		@DisplayName("logType = null")
		@Test
		void testLogType() throws Exception {
			TestController.logDtoExp.logType = null;
			postDtoWithResponceCode(400);
		}

		@DisplayName("artifact = ''")
		@Test
		void testArifactEmpty() throws Exception {
			TestController.logDtoExp.artifact = "";
			postDtoWithResponceCode(400);
		}
	}
}
