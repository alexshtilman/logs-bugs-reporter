/**
 * 
 */
package telran.logs.bugs.api;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
public interface LogsStatisticsApi {
	String STATISTICS = "/statistics";
	String ARTIFACT_AND_COUNT = "/artifact_and_count";
	String MOST_ENCOUNTERED_ARTIFACTS = "/most_encountered_artifacts";
	String MOST_ENCOUNTERED_EXCEPTIONS = "/most_encountered_exceptions";
	String LOGTYPE_AND_COUNT = "/logtype_and_count";
	String INTEGERS = "/integers_flux";
	String STRINGS_FLUX = "/strings_flux";
	String STRINGS_LIST = "/strings_list";
	String STRINGS_MONO = "/strings_mono";
}
