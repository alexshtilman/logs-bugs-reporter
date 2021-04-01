package telran.logs.bugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import lombok.SneakyThrows;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * 
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest
@Import({ TestChannelBinderConfiguration.class, MailSenderValidatorAutoConfiguration.class })
class EmailNotifierTest {

	private static final String EMAIL = "moshe@gmail.com";
	private static final String ASSIGNER_EMAIL = "teamlid@gmail.com";
	private static final String PROVIDER_ADDRESS = "http://localhost:1234";

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("log", "logs-bugs"));
	@MockBean
	EmailProviderClient client;

	@MockBean
	LoadBalancerComponent loadBalancer;

	@Autowired
	InputDestination input;

	@SneakyThrows
	@Test
	void normalFlow() {
		when(loadBalancer.getBaseUrl(anyString())).thenReturn(PROVIDER_ADDRESS);
		when(client.getEmailByArtifact(anyString(), anyString())).thenReturn(EMAIL);
		sendAndAssert(EMAIL, "Programmer");
	}

	@Test
	void normalNotAssigned() {
		when(loadBalancer.getBaseUrl(anyString())).thenReturn(PROVIDER_ADDRESS);
		when(client.getEmailByArtifact(anyString(), anyString())).thenReturn(null);
		when(client.getAssignerMail(anyString())).thenReturn(ASSIGNER_EMAIL);

		sendAndAssert(ASSIGNER_EMAIL, "Opened Bugs Assigner");
	}

	@SneakyThrows
	@Test
	void faildNotAssigned() {
		when(loadBalancer.getBaseUrl(anyString())).thenReturn(PROVIDER_ADDRESS);
		when(client.getEmailByArtifact(anyString(), anyString())).thenReturn(null);
		when(client.getAssignerMail(anyString())).thenReturn(null);

		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "POISON PILL", 999, "");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage[] messages = greenMail.getReceivedMessages();
		assertEquals(0, messages.length);

	}

	@SneakyThrows
	void sendAndAssert(String email, String assigner) {
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(email, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
		assertTrue(GreenMailUtil.getBody(message).contains(assigner));
	}

}