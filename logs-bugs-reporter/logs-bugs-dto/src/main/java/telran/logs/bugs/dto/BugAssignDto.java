/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@EqualsAndHashCode(callSuper = false)
public class BugAssignDto extends BugDto {
	@Min(value = 1)

	public long programmerId;

	public BugAssignDto(Seriousness seriousness, String description, LocalDate dateOpen, long programmerId) {
		super(seriousness, description, dateOpen);
		this.programmerId = programmerId;
	}

}
