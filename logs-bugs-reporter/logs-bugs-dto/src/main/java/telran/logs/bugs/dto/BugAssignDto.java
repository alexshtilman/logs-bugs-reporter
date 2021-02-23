/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class BugAssignDto extends BugDto {
	@Min(value = 1)

	public long programmerId;

	public BugAssignDto(Seriousness seriousness, String description, LocalDate dateOpen, long programmerId) {
		super(seriousness, description, dateOpen);
		this.programmerId = programmerId;
	}

}
