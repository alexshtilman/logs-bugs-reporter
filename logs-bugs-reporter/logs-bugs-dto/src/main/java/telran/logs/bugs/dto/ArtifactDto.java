/**
 * 
 */
package telran.logs.bugs.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public class ArtifactDto {
	@NotEmpty
	String artifactId;
	@Min(value = 1)
	long programmerId;
}
