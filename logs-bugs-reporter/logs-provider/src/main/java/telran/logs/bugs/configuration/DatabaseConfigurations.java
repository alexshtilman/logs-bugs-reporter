/**
 * 
 */
package telran.logs.bugs.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@ConfigurationProperties(prefix = "db")
public class DatabaseConfigurations {
	private Map<String, DatabaseConfiguration> configurations = new HashMap<>();

	// get/set ommitted

	public Map<Object, Object> createTargetDataSources() {
		Map<Object, Object> result = new HashMap<>();
		configurations.forEach((key, value) -> result.put(key, value.createDataSource()));
		return result;
	}
}