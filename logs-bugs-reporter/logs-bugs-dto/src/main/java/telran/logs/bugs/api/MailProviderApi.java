/**
 * 
 */
package telran.logs.bugs.api;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
public interface MailProviderApi {
	String MAIL = "/mail";
	String GET_ASIGNER_EMAIL = "/get_assigner_mail";
	String ARTIFACT = "/{artifact}";
}
