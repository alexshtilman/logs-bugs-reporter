/**
 * 
 */
package telran.aop;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LoggingComponentConfig.TestController.class)
@ContextConfiguration(classes = { LoggingComponentConfig.TestController.class })
@Import({ TestChannelBinderConfiguration.class, LoggingComponentConfig.class, })
@EnableAutoConfiguration
@TestPropertySource("classpath:componentTest.properties")
@Log4j2
class LoggerAspectTest {

	@Value("${app-binding-name}")
	String bindingName;

	@MockBean
	LoggingComponent loggingComponent;

	@Autowired
	OutputDestination consumer;

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	MockMvc mock;

	static LogDto logDtoExp = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, "artifact", 0, "");

	@Test
	void testNormal() {
		doNothing().when(loggingComponent).sendLog(any());

		try {
			mock.perform(post("/exeptions/post").contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(logDtoExp))).andReturn().getResponse().getStatus();
		} catch (Exception e) {

			Message<byte[]> message = consumer.receive(Long.MAX_VALUE, bindingName);
			assertNotNull(message);
			String json = new String(message.getPayload());
			log.debug("Recived from streamBrige {}, message {}", bindingName, json);
		}
	}

}
