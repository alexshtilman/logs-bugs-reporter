/**
 * 
 */
package telran.logs.bugs.configuration;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
public class DatabaseConfiguration {
	private String url;
	private String username;
	private String driver;
	private String password;
	// get/set ommitted

	public DataSource createDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}
}
