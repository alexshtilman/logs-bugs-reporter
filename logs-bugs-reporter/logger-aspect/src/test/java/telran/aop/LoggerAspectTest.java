/**
 * 
 */
package telran.aop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.rmi.ServerException;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Import({ TestChannelBinderConfiguration.class, LoggingComponentConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
class LoggerAspectTest {

	private final LoggerAspect aspect = new LoggerAspect();
	private TestController controllerProxy;

	@Value("${app-binding-name}")
	String bindingName;

	@Autowired
	LoggingComponent loggingComponent;

	@Autowired
	OutputDestination consumer;

	@MockBean
	StreamBridge brige;

	@BeforeAll
	public void setUp() {
		AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new TestController());
		aspectJProxyFactory.addAspect(aspect);

		DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
		AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

		controllerProxy = (TestController) aopProxy.getProxy();
		when(brige.send(any(), any())).thenReturn(true);
	}

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

	@Test
	void testNormal() throws Exception {
		String result = controllerProxy.putExceptionObject(new LogDto());
		System.out.println(result);
		// assertThat(result, is("ok"));
	}

}
