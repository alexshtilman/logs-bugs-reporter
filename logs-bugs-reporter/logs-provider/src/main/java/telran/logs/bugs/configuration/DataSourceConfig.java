/**
 * 
 */
package telran.logs.bugs.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@Configuration
public class DataSourceConfig {
	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create().username("main").password("37645nPP9090")
				.url("jdbc:postgresql://10.0.0.11:5432/java36")
				.driverClassName("org.postgresql.Driver").build();
	}

	@Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(@Qualifier("dsCustom") DataSource dsCustom) {
        return new JdbcTemplate(dsCustom);
    }
}