package telran.logs.bugs.client;

import static telran.logs.bugs.api.Constants.GET_ASIGNER_EMAIL;
import static telran.logs.bugs.api.Constants.MAIL_CONTROLLER;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class EmailProviderClient {

	RestTemplate restTemplate = new RestTemplate();

	public String getEmailByArtifact(String artifact, String urlArtifactMail) {
		ResponseEntity<String> respone = restTemplate.exchange(urlArtifactMail + MAIL_CONTROLLER + "/" + artifact,
				HttpMethod.GET, null, String.class);
		String result = respone.getBody();
		log.debug("got email by artifact {} {}", artifact, result);
		return result;
	}

	public String getAssignerMail(String urlAssignerMail) {
		ResponseEntity<String> respone = restTemplate.exchange(urlAssignerMail + MAIL_CONTROLLER + GET_ASIGNER_EMAIL,
				HttpMethod.GET, null, String.class);
		String result = respone.getBody();
		log.debug("got email {}", result);
		return result;
	}
}