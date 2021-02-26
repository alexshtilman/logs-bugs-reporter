/**
 * 
 */
package telran.logs.bugs.api;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */

public class Constants {
	// Common
	public static final String ALL = "/all";
	public static final String ID = "/{id}";

	// bugs
	public static final String BUGS_CONTROLLER = "/bugs";
	public static final String PROGRAMMERS = "/programmers";
	public static final String ARTIFACTS = "/artifacts";
	public static final String OPEN = "/open";
	public static final String CLOSE = "/close";

	public static final String ASSIGN = "/assign";
	public static final String EMAIL_BUGS_COUNTS = "/email_bugs_count";
	public static final String NON_ASSIGNED_BUGS_COUNTS = "/non_assigned_bugs_count";

	public static final String UNCLOSED_DURATION = "/unclosed_duration";
	public static final String MOST_BUGS = "/most_bugs";
	public static final String LEAST_BUGS = "/least_bugs";

	// Logs
	public static final String LOGS_CONTROLLER = "/logs";
	public static final String EXCEPTIONS = "/exceptions";
	public static final String BY_TYPE = "/by_type";

	// statistics
	public static final String STATISTICS_CONTROLLER = "/statistics";
	public static final String ARTIFACT_AND_COUNT = "/artifact_and_count";
	public static final String MOST_ENCOUNTERED_ARTIFACTS = "/most_encountered_artifacts";
	public static final String MOST_ENCOUNTERED_EXCEPTIONS = "/most_encountered_exceptions";
	public static final String LOGTYPE_AND_COUNT = "/logtype_and_count";
	public static final String INTEGERS = "/integers_flux";
	public static final String STRINGS_FLUX = "/strings_flux";
	public static final String STRINGS_LIST = "/strings_list";
	public static final String STRINGS_MONO = "/strings_mono";

	// mail
	public static final String MAIL_CONTROLLER = "/mail";
	public static final String GET_ASIGNER_EMAIL = "/get_assigner_mail";
	public static final String ARTIFACT = "/{artifact}";
}
