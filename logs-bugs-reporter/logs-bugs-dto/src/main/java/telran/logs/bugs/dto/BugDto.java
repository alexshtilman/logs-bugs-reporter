/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public class BugDto {
	@NotNull
	public Seriousness seriousness;
	@NotEmpty
	public String description;
	public LocalDate dateOpen;
}
