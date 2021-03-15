package telran.logs.bugs.services;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.client.LoadBalancerComponent;
import telran.logs.bugs.dto.LogDto;

@Service
@Log4j2
public class EmailNotifierService {
	@Autowired
	EmailProviderClient emailClient;
	@Autowired
	JavaMailSender mailSender;

	@Value("${subject:exception}")
	String subject;

	@Autowired
	LoadBalancerComponent loadBalancer;

	@Bean
	Consumer<LogDto> getExceptionsConsumer() {
		return this::takeLogAndSendMail;
	}

	void takeLogAndSendMail(LogDto logDto) {
		String person = "Programmer";

		String email = emailClient.getEmailByArtifact(logDto.artifact, loadBalancer.getBaseUrl("email-provider"));
		if (email == null || email.isEmpty()) {
			person = "Opened Bugs Assigner";
			email = emailClient.getAssignerMail(loadBalancer.getBaseUrl("assigner-email-provider"));
			if (email == null || email.isEmpty()) {
				log.error("Email `to` has been received neither from logs-bugs-email-provider "
						+ "nor from logs-bugs-assigner-mail-provider!");
				return;
			}
		}

		String text = String.format(
				"Hello, %s%n" + "Exception has been received%n" + "Date: %s%n" + "Exception type: %s%n"
						+ "Artifact: %s%n" + "Explanation: %s",
				person, logDto.dateTime, logDto.logType, logDto.artifact, logDto.result);
		log.debug(text);
		sendMail(email, subject, text);

	}

	private void sendMail(String email, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setTo(email);
		message.setText(text);
		mailSender.send(message);

	}

}
