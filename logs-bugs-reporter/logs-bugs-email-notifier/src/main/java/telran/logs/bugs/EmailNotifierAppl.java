package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
@Log4j2
public class EmailNotifierAppl {
	@Autowired
	EmailProviderClient emailClient;
	@Autowired
	JavaMailSender mailSender;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}

	@Bean
	Consumer<LogDto> getExceptionsConsumer() {
		return this::takeLogAndSendMail;
	}

	void takeLogAndSendMail(LogDto logDto) {
		String email = emailClient.getEmailByArtifact(logDto.artifact);
		String subject = "exception";
		String person = "Programmer";
		if (email == null) {
			person = "Opened Bugs Assigner";
			email = emailClient.getAssignerMail();
			if (email == null) {
				log.error("Email to has received neither from logs-bugs-email-provider "
						+ "nor from logs-bugs-assigner-mail-provider!");
				return;
			}
		}

		String text = String.format(
				"Hello, %s\n" + "Exception has been received\n" + "Date: %s \n" + "Exception type: %s\n"
						+ "Artifact: %s\n" + "Explanation: %s",
				person, logDto.dateTime, logDto.logType, logDto.artifact, logDto.result);

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