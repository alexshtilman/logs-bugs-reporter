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

@SpringBootTest
@Import({ TestChannelBinderConfiguration.class, MailSenderValidatorAutoConfiguration.class })
class EmailNotifierTest {

	private static final String EMAIL = "moshe@gmail.com";
	private static final String ASSIGNER_EMAIL = "teamlid@gmail.com";

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("log", "logs-bugs"));
	@MockBean
	EmailProviderClient client;
	@Autowired
	InputDestination input;

	@SneakyThrows
	@Test
	void normalFlow() {
		when(client.getEmailByArtifact(anyString())).thenReturn(EMAIL);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
		assertTrue(GreenMailUtil.getBody(message).contains("Programmer"));

	}

	@SneakyThrows
	@Test
	void normalNotAssigned() {
		// Assume that we don't know
		when(client.getEmailByArtifact(anyString())).thenReturn(null);
		when(client.getAssignerMail()).thenReturn(ASSIGNER_EMAIL);

		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "UNKNOWN ARTIFACT", 0, "");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(ASSIGNER_EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
		assertTrue(GreenMailUtil.getBody(message).contains("Opened Bugs Assigner"));

	}

	@SneakyThrows
	@Test
	void faildNotAssigned() {

		when(client.getEmailByArtifact(anyString())).thenReturn(null);
		when(client.getAssignerMail()).thenReturn(null);

		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "POISON PILL", 999, "");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage[] messages = greenMail.getReceivedMessages();
		assertEquals(0, messages.length);

	}
}