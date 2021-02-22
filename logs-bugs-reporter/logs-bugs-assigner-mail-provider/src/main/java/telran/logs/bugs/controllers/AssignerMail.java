/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.MailProviderApi.GET_ASIGNER_EMAIL;
import static telran.logs.bugs.api.MailProviderApi.MAIL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@RestController
@RequestMapping(MAIL)
public class AssignerMail {
	@Value("${default-assigner-email}")
	String defaultAssgnerEmail;

	@GetMapping(GET_ASIGNER_EMAIL)
	public String getAssignerMail() {
		return defaultAssgnerEmail;
	}

}
